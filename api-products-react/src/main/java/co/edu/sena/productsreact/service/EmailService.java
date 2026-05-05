package co.edu.sena.productsreact.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EmailService {

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    public boolean sendPasswordResetEmail(String toEmail, String resetLink) {
        if (!mailEnabled || username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        try {
            sendSmtpMessage(toEmail, resetLink);
            return true;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo enviar el correo de recuperacion", ex);
        }
    }

    private void sendSmtpMessage(String toEmail, String resetLink) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            SmtpConnection smtp = new SmtpConnection(socket);
            smtp.expect(220);
            smtp.command("EHLO localhost", 250);
            smtp.command("STARTTLS", 220);

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (Socket secureSocket = sslSocketFactory.createSocket(socket, host, port, true)) {
                SmtpConnection secureSmtp = new SmtpConnection(secureSocket);
                secureSmtp.command("EHLO localhost", 250);
                secureSmtp.command("AUTH LOGIN", 334);
                secureSmtp.command(encode(username), 334);
                secureSmtp.command(encode(password), 235);
                secureSmtp.command("MAIL FROM:<" + username + ">", 250);
                secureSmtp.command("RCPT TO:<" + toEmail + ">", 250);
                secureSmtp.command("DATA", 354);
                secureSmtp.writeData(buildMessage(toEmail, resetLink));
                secureSmtp.expect(250);
                secureSmtp.command("QUIT", 221);
            }
        }
    }

    private String buildMessage(String toEmail, String resetLink) {
        return """
                From: EFORM <%s>
                To: <%s>
                Subject: Recuperacion de contrasena - EFORM
                Content-Type: text/plain; charset=UTF-8

                Hola,

                Recibimos una solicitud para recuperar tu contrasena.

                Abre este enlace para crear una nueva contrasena:
                %s

                Este enlace vence en 30 minutos.

                Si no solicitaste este cambio, puedes ignorar este mensaje.
                """.formatted(username, toEmail, resetLink);
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static class SmtpConnection {
        private final BufferedReader reader;
        private final BufferedWriter writer;

        SmtpConnection(Socket socket) throws IOException {
            this.reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8));
        }

        void command(String command, int expectedCode) throws IOException {
            writer.write(command + "\r\n");
            writer.flush();
            expect(expectedCode);
        }

        void writeData(String data) throws IOException {
            writer.write(data.replace("\n", "\r\n"));
            writer.write("\r\n.\r\n");
            writer.flush();
        }

        void expect(int expectedCode) throws IOException {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("El servidor SMTP cerro la conexion");
            }

            String lastLine = line;
            while (line.length() > 3 && line.charAt(3) == '-') {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                lastLine = line;
            }

            if (!lastLine.startsWith(String.valueOf(expectedCode))) {
                throw new IOException("Respuesta SMTP inesperada: " + lastLine);
            }
        }
    }
}
