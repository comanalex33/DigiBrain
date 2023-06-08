using DigiBrainServer.Models;
using MailKit.Net.Smtp;
using MailKit.Security;
using MimeKit;
using MimeKit.Text;

namespace DigiBrainServer.Services
{
    public interface IEmailService
    {
        void Send(string to, string subject, string html, string from = null);
    }

    public class EmailService : IEmailService
    {
        private readonly SecretsModel _secretsManager;

        public EmailService(SecretsModel secretsManager)
        {
            _secretsManager = secretsManager;
        }

        public void Send(string to, string subject, string html, string from = null)
        {
            var email = new MimeMessage();
            email.From.Add(MailboxAddress.Parse(from ?? _secretsManager.EmailFrom));
            email.To.Add(MailboxAddress.Parse(to));
            email.Subject = subject;
            email.Body = new TextPart(TextFormat.Html) { Text = html };

            using var smtp = new SmtpClient();
            smtp.Connect(_secretsManager.SmtpHost, _secretsManager.SmtpPort, SecureSocketOptions.StartTls);
            smtp.Authenticate(_secretsManager.SmtpUser, _secretsManager.SmtpPassword);
            smtp.Send(email);
            smtp.Disconnect(true);
        }
    }
}
