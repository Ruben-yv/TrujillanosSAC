package com.example.TrujillanosSAC.Config;

import com.example.TrujillanosSAC.Model.Cargo;
import com.example.TrujillanosSAC.Model.Empleado;
import com.example.TrujillanosSAC.Model.Persona;
import com.example.TrujillanosSAC.Model.Rol;
import com.example.TrujillanosSAC.Model.TipoDocumento;
import com.example.TrujillanosSAC.Model.Usuario;
import com.example.TrujillanosSAC.Repository.CargoRepository;
import com.example.TrujillanosSAC.Repository.EmpleadoRepository;
import com.example.TrujillanosSAC.Repository.PersonaRepository;
import com.example.TrujillanosSAC.Repository.RolRepository;
import com.example.TrujillanosSAC.Repository.TipoDocumentoRepository;
import com.example.TrujillanosSAC.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Configuration
public class AdminUserSeeder {

    @Bean
    @Transactional
    CommandLineRunner crearUsuarioAdmin(UsuarioRepository usuarioRepository,
                                         RolRepository rolRepository,
                                         CargoRepository cargoRepository,
                                         TipoDocumentoRepository tipoDocumentoRepository,
                                         PersonaRepository personaRepository,
                                         EmpleadoRepository empleadoRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.existsByUsername("admin")) {
                return;
            }

            Rol rolEmpleado = rolRepository.findByNombreIgnoreCase("EMPLEADO")
                    .orElseGet(() -> rolRepository.save(Rol.builder().nombre("EMPLEADO").build()));

            Cargo cargoAdmin = cargoRepository.findByNombreIgnoreCase("ADMIN")
                    .orElseGet(() -> cargoRepository.save(Cargo.builder().nombre("ADMIN").build()));

            TipoDocumento dni = tipoDocumentoRepository.findByAbreviaturaIgnoreCase("DNI")
                    .orElseGet(() -> tipoDocumentoRepository.save(TipoDocumento.builder()
                            .nombre("DNI")
                            .abreviatura("DNI")
                            .longitud(8)
                            .build()));

            Persona persona = personaRepository.findByNumDoc("00000000")
                    .orElseGet(() -> personaRepository.save(Persona.builder()
                            .nombre("Admin")
                            .apellido("Sistema")
                            .telefono("999999999")
                            .documento(dni)
                            .numDoc("00000000")
                            .build()));

            if (!empleadoRepository.existsById(persona.getIdPersona())) {
                empleadoRepository.save(Empleado.builder()
                        .persona(persona)
                        .salario(BigDecimal.ZERO)
                        .fechaContratacion(LocalDate.now())
                        .cargo(cargoAdmin)
                        .build());
            }

            usuarioRepository.save(Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .persona(persona)
                    .roles(Set.of(rolEmpleado))
                    .build());
        };
    }
}
