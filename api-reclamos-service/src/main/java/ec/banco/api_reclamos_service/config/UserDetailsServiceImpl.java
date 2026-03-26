package ec.banco.api_reclamos_service.config;

import ec.banco.api_reclamos_service.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String identificacion) throws UsernameNotFoundException {
        var cliente = clienteRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Cliente no encontrado con identificación: " + identificacion));

        return User.builder()
                .username(cliente.getIdentificacion())
                .password(cliente.getPassword())
                .disabled(!cliente.getActivo())
                .authorities(Collections.emptyList())
                .build();
    }
}
