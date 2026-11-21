package cl.duoc.ms_vidasalud_bff.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * La IP que el BFF le informa a appointments (y que termina en AUD_EVENTO) no la decide el cliente:
 * con N proxies de confianza es la entrada N contando desde el final de X-Forwarded-For; sin proxies,
 * la IP de la conexión.
 */
class IpDelClienteTest {

    private static final String IP_CONEXION = "190.20.30.40";

    @Test
    void sinProxies_ignoraLaCabeceraDelCliente() {
        // El cliente inventa la cabecera; sin proxies delante, solo vale la IP de la conexión.
        MockHttpServletRequest request = peticion("8.8.8.8", IP_CONEXION);

        assertThat(ClientesHttpConfig.ipDelCliente(request, 0)).isEqualTo(IP_CONEXION);
    }

    @Test
    void sinProxies_sinCabecera_devuelveLaIpDeLaConexion() {
        MockHttpServletRequest request = peticion(null, IP_CONEXION);

        assertThat(ClientesHttpConfig.ipDelCliente(request, 0)).isEqualTo(IP_CONEXION);
    }

    @Test
    void unProxy_tomaLaUltimaEntrada() {
        // El API Gateway agrega al final la IP real; lo de la izquierda lo escribió el cliente.
        MockHttpServletRequest request = peticion("8.8.8.8, 190.20.30.40", "10.0.0.5");

        assertThat(ClientesHttpConfig.ipDelCliente(request, 1)).isEqualTo("190.20.30.40");
    }

    @Test
    void dosProxies_tomaLaPenultimaEntrada() {
        MockHttpServletRequest request = peticion("8.8.8.8, 190.20.30.40, 10.0.0.8", "10.0.0.9");

        assertThat(ClientesHttpConfig.ipDelCliente(request, 2)).isEqualTo("190.20.30.40");
    }

    @Test
    void cadenaMasCortaDeLoEsperado_devuelveLaIpDeLaConexion() {
        // Se esperaban dos proxies pero llegó una sola entrada.
        MockHttpServletRequest unaEntrada = peticion("8.8.8.8", IP_CONEXION);
        assertThat(ClientesHttpConfig.ipDelCliente(unaEntrada, 2)).isEqualTo(IP_CONEXION);

        // Se esperaba un proxy pero no llegó la cabecera.
        MockHttpServletRequest sinCabecera = peticion(null, IP_CONEXION);
        assertThat(ClientesHttpConfig.ipDelCliente(sinCabecera, 1)).isEqualTo(IP_CONEXION);
    }

    private static MockHttpServletRequest peticion(String xForwardedFor, String remoteAddr) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        if (xForwardedFor != null) {
            request.addHeader(ClientesHttpConfig.CABECERA_IP, xForwardedFor);
        }
        return request;
    }
}
