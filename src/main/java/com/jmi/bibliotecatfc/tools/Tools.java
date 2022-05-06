package com.jmi.bibliotecatfc.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import com.jmi.bibliotecatfc.exceptions.BibliotecaException;

public class Tools {
    // de 1 genero 000001A (seguido de la letra del NIF)

    public static String creaSerie(int a) {
        return String.format("%06d%c", a, calculaLetra(a));
    }

    private static char calculaLetra(int dni) {
        String juegoCaracteres = "TRWAGMYFPDXBNJZSQVHLCKE";
        return juegoCaracteres.charAt(dni % 23);
    }

    // Esta versión genera un pdf con el código de barras cod39 del valor de parámetro code
    // EL fichero generado se guarda con el nombre Tabla seguido de _ y valor de code (terminado .pdf)
    //   Ejem: Para Tabla=Socio y code=000001A , el nombre sería Socio_000001A.pdf
    // La ruta siempre debe terminar en /
    public static String creaFicheroPDF(String ruta, String Tabla, String code) throws DocumentException, FileNotFoundException {
        String file = ruta + Tabla + "_" + code + ".pdf";
        FileOutputStream archivo = new FileOutputStream(file);
        Rectangle pagesize = new Rectangle(100, 50);
        Document documento = new Document(pagesize);
        documento.setMargins(5, 5, 5, 5);
        PdfWriter pdfw = PdfWriter.getInstance(documento, archivo);
        documento.open();

        PdfContentByte cb = pdfw.getDirectContent();

        Barcode39 code39 = new Barcode39();
        code39.setCode(code);
        //documento.add(new Paragraph("Imprime y recorta el siguiente código de barras", new Font(Font.FontFamily.HELVETICA, 12)));
        //documento.add(new Paragraph("Pégalo en el libro con un poco de celofán ancho", new Font(Font.FontFamily.HELVETICA, 12)));
        //documento.add(new Paragraph("  ", new Font(Font.FontFamily.HELVETICA, 12)));
        //documento.add(new Paragraph("  ", new Font(Font.FontFamily.HELVETICA, 12)));
        documento.add(code39.createImageWithBarcode(cb, null, null));
        documento.close();
        return file;
    }

    // En esta versión evitamos el uso de fichero y generamos el pdf al vuelo
    // >> SIN HACER
    // Converte a String e.printStackTrace para un objeto Exception
    public static String printStackTrace_toString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    // Validación de captcha de Google
    public static boolean validarCaptcha(String gRecaptchaResponse) {
        // Poner la clave secreta obtenida al dar de alta el Captcha en Google
        String secret = "6LdMoW8dAAAAAPPOnatoxfIWMfbbLpAQNaGloV6P";
        String urlCaptcha = "https://www.google.com/recaptcha/api/siteverify?secret=" + secret + "&response=" + gRecaptchaResponse;
        if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
            return false;
        }
        try {
            URL url = new URL(urlCaptcha);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer connectionResponse = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                connectionResponse.append(inputLine);
            }
            in.close();
            System.out.println("Response from google Recaptcha service=" + connectionResponse);
            JsonReader jsonReader = Json.createReader(new StringReader(connectionResponse.toString()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mira si la variable de entorno TRAZA está definida
    // Si está definida y su valor es true devuelve true, resto casos false
    // >>> No se si es buena idea porque la JVM debería heredar las variables 
    // de entorno la iniciar pero para ello tendría que reiniciar incluso Payara
    // por lo que no es muy dinámico
    // creo que será mejor ponerlo en un archivo o en una propiedad de Payara
    // pero que la lea de forma dinámica
    public static boolean valorTRAZA() {
        String valor = System.getenv().get("TRAZA");
        if (valor == null) {
            return false;
        }
        valor = valor.toLowerCase();
        return valor.equals("true");
    }

    // Permite enviar correo desde Gmail usando cuentas con el modo "inseguro" activo
    public static void enviarConGMail(String destinatario, String asunto, String cuerpo) {
        String remitente = "no_reply@ies-azarquiel.es";
        String clave = "Azarquiel2021";

        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true"); // Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); // Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "587"); // El puerto SMTP seguro de Google

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.addRecipients(Message.RecipientType.TO, destinatario);// Se podrían añadir varios de la misma manera
            message.setSubject(asunto);
            // Para poner texto plano
            //message.setText(cuerpo);
            // Si no pones este encabezado no interpreta el html
            message.setContent(cuerpo, "text/html");

            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", remitente, clave);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException me) {
            me.printStackTrace(); // Si se produce un error
        }
    }

    public static String creaCuerpoCorreo(String token) {
        String ref = "http://localhost:8080/biblioteca/controller?operacion=validacion&token=" + token;
        StringBuilder st = new StringBuilder();
        st.append("<!DOCTYPE html>");
        st.append("<html lang='es'>");
        st.append("  <head>");
        st.append("    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        st.append("   <title>Correo de IES Azarquiel</title>");
        st.append(" </head>");
        st.append(" <body>");
        st.append(" Recibes este correo porque te has registrado en <br>");
        st.append(" la  aplicación  Biblioteca.<br><br>");
        st.append(" Para  poder  recibir la contraseña de acceso<br>");
        st.append(" debes validar antes el correo con  el que te<br>");
        st.append(" registraste.<br><br>");
        st.append("   Pulsa en el siguiente enlace <br><br>");
        st.append(" <a href='" + ref + "'> Enlace </a>");
        st.append(" </body>");
        st.append("</html>");
        return st.toString();
    }

    //>> NOTA: Para la próxima mejor usar API cliente de Apache Http
    // Envía un sms con el texto msg al destino dest
    // dest - número español de 9 digitos empezando por 6 o 7
    // msg - máximo de 70 caracteres sino se trunca
    public static void enviarSMS(String dest, String msg) throws IOException, BibliotecaException {
        // Control de errores sobre dest
        if (dest == null || !dest.matches("[67]\\d{8}")) {
            throw new BibliotecaException(" Número teléfono erróneo", 7);
        }
        // Recibimos dest sin prefijo del pais, añadimos 34
        dest = "34" + dest;
        if (msg == null) {
            throw new BibliotecaException(" SMS sin mensaje", 8);
        }
        if (msg.length() > 69) {
            msg = msg.substring(0, 69);
        }

        URL obj = new URL("http://www.altiria.net/api/http");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // Uso post y quiero respuesta
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        // Contruyo la QueryString
        Map<String, String> arguments = new HashMap<>();
        arguments.put("cmd", "sendsms");
        arguments.put("login", "no_reply@ies-azarquiel.es");
        arguments.put("passwd", "sms!aza#biblioteca");
        arguments.put("dest", dest);
        arguments.put("msg", msg);
        // Debe coincidir con el configurado en el usuario
        arguments.put("senderId", "AZARQUIEL");
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        // petición HTTP
        con.setFixedLengthStreamingMode(length);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.connect();
        OutputStream os = con.getOutputStream();
        os.write(out);
        // Miro si la respuesta es ok
        int responseCode = con.getResponseCode();
        System.out.println("Codigo de respuesta : " + responseCode);
        if (responseCode != 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer respuesta = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                respuesta.append(inputLine);
            }
            in.close();
            throw new BibliotecaException("error en envío de mensaje SMS " + respuesta.toString(), 6);
        }
    }

    // Genero un string aleatorio de ancho caracteres
    // Si queremos sólo número paso como parámetros solonum a true
    private static String generaAleatorio(int ancho, boolean solonum) throws Exception {
        // En caso de claves se debería quitar la i y la l porque se pueden confundir con
        // el 1 dependiendo de la fuente
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        String DATA_FOR_RANDOM_STRING = NUMBER;
        if (!solonum) {
            DATA_FOR_RANDOM_STRING += CHAR_LOWER + CHAR_UPPER;
        }
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(ancho);
        for (int i = 0; i < ancho; i++) {
            // 0-62 (exclusive), retornos aleatorios 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();
    }

    // Sobrecarga en caso de querer números y letras
    private static String generaAleatorio(int ancho) throws Exception {
        return generaAleatorio(ancho, false);
    }

    public static String generaToken() throws Exception {
        return generaAleatorio(30);
    }

    public static String generaClave() throws Exception {
        return generaAleatorio(6, true);
    }

}
