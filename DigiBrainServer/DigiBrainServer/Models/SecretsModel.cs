
namespace DigiBrainServer.Models
{
    public class SecretsModel
    {
        // Database secrets
        public string DatabaseHost { get; set; }
        public string DatabaseName { get; set; }
        public string DatabasePassword { get; set; }
        public string JWTSecret { get; set; }

        // S3 bucket secrets
        public string S3ReadAccessKey { get; set; }
        public string S3ReadSecretKey { get; set; }
        public string S3WriteAccessKey { get; set; }
        public string S3WriteSecretKey { get; set; }
        public string S3BucketName { get; set; }
        public string S3BucketRegion { get; set; }

        // Email service secrets
        public string EmailFrom { get; set; }
        public string SmtpHost { get; set; }
        public int SmtpPort { get; set; }
        public string SmtpUser { get; set; }
        public string SmtpPassword { get; set; }
    }
}
