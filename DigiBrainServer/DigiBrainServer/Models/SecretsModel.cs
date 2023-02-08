
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
    }
}
