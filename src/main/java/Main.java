package co.com;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

@Component
public class Main
{
    final static String URL_TOKEN = "http://localhost:8080/token";
    final static String URL_AFILIACIONES = "http://localhost:8080/AfiliacionARL";
    final static String URL_EMPRESAS = "http://localhost:8080/ConsultaEmpresasTrasladadas";
    final static String URL_ESTRUCTURA_EMPRESAS = "http://localhost:8080/ConsultaEstructuraEmpresa";

    final static String URL_INICIO_RELACION_LABORAL = "http://localhost:8080/InicioRelacionLaboralARL";
    final static String URL_TERMINACION_RELACION_LABORAL = "http://localhost:8080/TerminacionRelacionLaboralARL";
    final static String URL_TRASLADO = "http://localhost:8080/TrasladoEmpleador";
    final static String URL_RETRACTACION_TRASLADO = "http://localhost:8080/RetractoTrasladoEmpleador";

    final static String URL_RECLASIFICACION_CENTRO_TRABAJO = "http://localhost:8080/ReclasificacionCentroTrabajo";
    final static String URL_RETIRO_DEFINITIVO = "http://localhost:8080/RetiroDefinitivoEmpresaSGRL";
    final static String URL_MODIFICACION_IBC = "http://localhost:8080/ModificacionIBC";

    final static String URL_NOVEDAD_CENTRO_TRABAJO = "http://localhost:8080/NovedadesCentroTrabajo";
    final static String URL_NOVEDAD_SEDES= "http://localhost:8080/NovedadesSedes";
    final static String URL_NOVEDAD_TRANSITORIAS = "http://localhost:8080/NovedadesTransitorias";
    final static String URL_REPORTE_MORA = "http://localhost:8080/ReporteMora";

    final static String CONTENT_TYPE = "Content-Type";
    final static String AUTHORIZATION = "Authorization";
    final static String BEARER = "bearer ";
    final static String CONTENT_TYPE_JSON = "application/json";
    final static String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    static Logger log = LoggerFactory.getLogger(Main.class);
    static ConfigFile configFile;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0/1 * * * * *")
    public void prueba()
    {
        log.info("CRON PRUEBA. El tiempo es {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0/45 * * * * *")
    public void task()
    {
        log.info("El tiempo es {}", dateFormat.format(new Date()));
        log.info("Empezamos con el flujo...");
        try
        {
            log.info("Consultamos properties...");
            configFile = new ConfigFile();
            log.info("Properties consultado correctamente!");
            configFile.printStringProp();

            log.info("Token...");
            String token = token();
            log.info("Consumo token exitoso!");
            log.info(token);

            log.info("Afiliaciones...");
            JSONObject afiliaciones = afiliaciones(token);
            log.info("Consumo afiliaciones exitoso!");
            log.info(afiliaciones.toString());

            log.info("Centros de trabajo novedades...");
            JSONObject novedadesCentro = novedadesCentro(token);
            log.info("Consumo modificacionIBC exitoso!");
            log.info(novedadesCentro.toString());

            log.info("Sedes novedades...");
            JSONObject novedadesSede = novedadesSede(token);
            log.info("Consumo modificacionIBC exitoso!");
            log.info(novedadesSede.toString());

            log.info("Inicio relacion laboral...");
            JSONObject inicioRelacionLaboral = inicioRelacionLaboral(token);
            log.info("Consumo Inicio relacion laboral exitoso!");
            log.info(inicioRelacionLaboral.toString());

            log.info("terminacionRelacionLaboral...");
            JSONObject terminacionRelacionLaboral = terminacionRelacionLaboral(token);
            log.info("Consumo terminacionRelacionLaboral exitoso!");
            log.info(terminacionRelacionLaboral.toString());

            log.info("translado...");
            JSONObject translado = translado(token);
            log.info("Consumo translado exitoso!");
            log.info(translado.toString());

            log.info("retractacionTranslado...");
            JSONObject retractacionTranslado = retractacionTranslado(token);
            log.info("Consumo retractacionTranslado exitoso!");
            log.info(retractacionTranslado.toString());

            log.info("retiroDefinitivo...");
            JSONObject retiroDefinitivo = retiroDefinitivo(token);
            log.info("Consumo retiroDefinitivo exitoso!");
            log.info(retiroDefinitivo.toString());

            log.info("Token Otravez...");
            token = token();
            log.info("Consumo token exitoso again!");
            log.info(token);

            log.info("reclasificacionCentroTrabajo...");
            JSONObject reclasificacionCentroTrabajo = reclasificacionCentroTrabajo(token);
            log.info("Consumo reclasificacionCentroTrabajo exitoso!");
            log.info(reclasificacionCentroTrabajo.toString());

            log.info("Modificacion IBC...");
            JSONObject modificacionIBC = modificacionIBC(token);
            log.info("Consumo modificacionIBC exitoso!");
            log.info(modificacionIBC.toString());

            log.info("Novedades transitorias...");
            JSONObject novedadesTransitorias = novedadesTransitorias(token);
            log.info("Consumo modificacionIBC exitoso!");
            log.info(novedadesTransitorias.toString());

            log.info("Reportemora...");
            JSONObject reporteMora = reporteMora(token);
            log.info("Consumo Reportemora exitoso!");
            log.info(reporteMora.toString());

            log.info("Consulta empresa...");
            JSONObject consultaEmpresas = consultaEmpresas(token);
            log.info("Consumo consultaEmpresa exitoso!");
            log.info(consultaEmpresas.toString());

            log.info("Consulta estructura empresa...");
            JSONObject consultaEstructuras = consultaEstructuraEmpresas(token);
            log.info("Consumo consultaEstructuraEmpresa exitoso!");
            log.info(consultaEstructuras.toString());

        } catch (IOException e)
        {
            log.error("Error en el flujo: ".concat(e.getMessage()));
        }
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private String token() throws IOException
    {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        HttpPost post = new HttpPost(URL_TOKEN);
        post.setHeader(CONTENT_TYPE, "application/x-www-form-urlencoded");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post))
        {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                String json_string = EntityUtils.toString(response.getEntity());
                JSONObject result = new JSONObject(json_string);
                return result.getString("access_token");
            }else
            {
                throw new IllegalStateException("Error en el token: ".concat(status_code + ""));

            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject afiliaciones(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_AFILIACIONES);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las afiliaciones: ".concat(status_code + ""));
            }
        }
    }


    /**
     *
     * @return
     * @throws IOException
     */
    private JSONObject consultaEmpresas(String token) throws IOException
    {
        JSONObject request = new JSONObject();
        final String codigoARL      = "CodigoARL";
        final String fechaInicio    = "FechaInicio";
        final String fechaFin       = "FechaFin";

        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-dd-MM"));

        request.put(codigoARL, configFile.getProp(codigoARL));
        request.put(fechaInicio, configFile.getProp(fechaInicio));
        request.put(fechaFin, LocalDate.now().toString());

        HttpPost post = new HttpPost(URL_EMPRESAS);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        post.setEntity(new ByteArrayEntity(request.toString().getBytes()));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");

        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post))
        {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());
            }else
            {
                throw new IllegalStateException("Error en el consultaEmpresas: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject consultaEstructuraEmpresas(String token) throws IOException
    {

        HttpPost post = new HttpPost(URL_ESTRUCTURA_EMPRESAS);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");

        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post))
        {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }
                }

                return new JSONObject(sb.toString());
            }else
            {
                throw new IllegalStateException("Error en el consultaEstructuraEmpresas: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject inicioRelacionLaboral(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_INICIO_RELACION_LABORAL);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las inicioRelacionLaboral: ".concat(status_code + ""));
            }
        }
    }


    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject terminacionRelacionLaboral(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_TERMINACION_RELACION_LABORAL);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las terminacionRelacionLaboral: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject translado(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_TRASLADO);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las translado: ".concat(status_code + ""));
            }
        }
    }


    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject retractacionTranslado(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_RETRACTACION_TRASLADO);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las retractacionTranslado: ".concat(status_code + ""));
            }
        }
    }


    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject retiroDefinitivo(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_RETIRO_DEFINITIVO);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las retiroDefinitivo: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject reclasificacionCentroTrabajo(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_RECLASIFICACION_CENTRO_TRABAJO);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las reclasificacionCentroTrabajo: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject modificacionIBC(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_MODIFICACION_IBC);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las modificacionIBC: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject novedadesSede(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_NOVEDAD_SEDES);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las NovedadesSedes: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject novedadesCentro(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_NOVEDAD_CENTRO_TRABAJO);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las novedadesCentro: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject novedadesTransitorias(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_NOVEDAD_TRANSITORIAS);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en las novedadesTransitorias: ".concat(status_code + ""));
            }
        }
    }

    /**
     *
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject reporteMora(String token) throws IOException
    {
        HttpPost post = new HttpPost(URL_REPORTE_MORA);
        post.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(AUTHORIZATION, BEARER.concat(token));
        StringBuilder sb = new StringBuilder("{ok:'ok'}");
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            int status_code = response.getStatusLine().getStatusCode();
            if(status_code >= 200 && status_code <= 204)
            {
                if(response.getEntity() != null && response.getEntity().getContentLength() > 0)
                {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }

                }

                return new JSONObject(sb.toString());

            }else
            {
                throw new IllegalStateException("Error en el reporteMora: ".concat(status_code + ""));
            }
        }
    }


}
