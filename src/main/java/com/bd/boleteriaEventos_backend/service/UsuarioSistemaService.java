package com.bd.boleteriaEventos_backend.service;

import com.bd.boleteriaEventos_backend.dto.LoginRequestDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaCreateDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaResponseDTO;
import com.bd.boleteriaEventos_backend.dto.UsuarioSistemaUpdateDTO;
import com.bd.boleteriaEventos_backend.entity.Rol;
import com.bd.boleteriaEventos_backend.entity.UsuarioSistema;
import com.bd.boleteriaEventos_backend.exception.BusinessRuleException;
import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;
import com.bd.boleteriaEventos_backend.repository.RolRepository;
import com.bd.boleteriaEventos_backend.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioSistemaService {

    private static final String PREFIJO_CLAVE_LARGA = "{sha384+bcrypt}";
    private static final BCryptPasswordEncoder CODIFICADOR = new BCryptPasswordEncoder();
    private static final Pattern HASH_BCRYPT = Pattern.compile("\\$2[aby]?\\$[0-9]{2}\\$[./A-Za-z0-9]{53}");
    private static final String HASH_FICTICIO = CODIFICADOR.encode(UUID.randomUUID().toString());

    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final RolRepository rolRepository;

    @Transactional
    public UsuarioSistemaResponseDTO crear(UsuarioSistemaCreateDTO dto) {
        if (usuarioSistemaRepository.findByCorreoIgnoreCase(dto.correo().trim()).isPresent()) {
            throw new BusinessRuleException("Ya existe un usuario con el correo: " + dto.correo());
        }

        Rol rol = rolRepository.findById(dto.idRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + dto.idRol()));

        UsuarioSistema usuario = UsuarioSistema.builder()
                .correo(dto.correo().trim().toLowerCase())
                .claveHash(codificar(dto.clave()))
                .estado(dto.estado().trim().toUpperCase())
                .rol(rol)
                .build();

        return mapToDTO(usuarioSistemaRepository.save(usuario));
    }

    public Optional<UsuarioSistemaResponseDTO> autenticar(LoginRequestDTO dto) {
        boolean larga = excedeLimiteBCrypt(dto.clave());
        String clave = larga ? resumirClave(dto.clave()) : dto.clave();
        Optional<UsuarioSistema> usuario = usuarioSistemaRepository.findByCorreoIgnoreCase(dto.correo().trim())
                .filter(u -> "ACTIVO".equals(u.getEstado()))
                .filter(u -> hashCompatible(u.getClaveHash(), larga));
        String hash = usuario.map(UsuarioSistema::getClaveHash).orElse(HASH_FICTICIO);
        if (hash.startsWith(PREFIJO_CLAVE_LARGA)) {
            hash = hash.substring(PREFIJO_CLAVE_LARGA.length());
        }
        return CODIFICADOR.matches(clave, hash) ? usuario.map(this::mapToDTO) : Optional.empty();
    }

    private String codificar(String clave) {
        if (excedeLimiteBCrypt(clave)) {
            return PREFIJO_CLAVE_LARGA + CODIFICADOR.encode(resumirClave(clave));
        }
        return CODIFICADOR.encode(clave);
    }

    private boolean hashCompatible(String hash, boolean larga) {
        if (hash == null || hash.startsWith(PREFIJO_CLAVE_LARGA) != larga) {
            return false;
        }
        return HASH_BCRYPT.matcher(larga ? hash.substring(PREFIJO_CLAVE_LARGA.length()) : hash).matches();
    }

    private boolean excedeLimiteBCrypt(String clave) {
        return clave.getBytes(StandardCharsets.UTF_8).length > 72;
    }

    private String resumirClave(String clave) {
        try {
            // El resumen de 64 caracteres conserva las claves largas admitidas por el registro.
            MessageDigest resumen = MessageDigest.getInstance("SHA-384");
            resumen.update("boleteria-eventos/clave/v1:".getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(resumen.digest(clave.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-384 no está disponible", ex);
        }
    }

    @Transactional
    public UsuarioSistemaResponseDTO actualizar(Long id, UsuarioSistemaUpdateDTO dto) {
        UsuarioSistema registro = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        String correo = dto.correo().trim().toLowerCase(Locale.ROOT);
        if (usuarioSistemaRepository.findByCorreoIgnoreCase(correo)
                .filter(usuario -> !id.equals(usuario.getIdUsuario())).isPresent()) {
            throw new BusinessRuleException("Ya existe un usuario con el correo: " + correo);
        }
        registro.setRol(rolRepository.findById(dto.idRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + dto.idRol())));
        registro.setCorreo(correo);
        registro.setEstado(dto.estado().trim().toUpperCase(Locale.ROOT));
        try {
            usuarioSistemaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("No se pudo actualizar el usuario por un conflicto de integridad");
        }
        return mapToDTO(registro);
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            if (usuarioSistemaRepository.eliminarSinCascada(id) == 0) {
                throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("El registro tiene dependencias y no se puede eliminar");
        }
    }

    public List<UsuarioSistemaResponseDTO> listarTodos() {
        return usuarioSistemaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public UsuarioSistemaResponseDTO obtenerPorId(Long id) {
        UsuarioSistema u = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario del sistema no encontrado con ID: " + id));
        return mapToDTO(u);
    }

    public UsuarioSistemaResponseDTO mapToDTO(UsuarioSistema u) {
        return new UsuarioSistemaResponseDTO(
                u.getIdUsuario(),
                u.getCorreo(),
                u.getEstado(),
                u.getRol() != null ? u.getRol().getIdRol() : null,
                u.getRol() != null ? u.getRol().getNombre() : null
        );
    }
}
