using Amazon.Runtime;
using Amazon.S3;
using Amazon.S3.Transfer;
using DigiBrainServer.Models;
using Microsoft.AspNetCore.Http;
using System;
using System.IO;
using System.Security.Cryptography;
using System.Threading.Tasks;

namespace DigiBrainServer.Services
{
    public class S3Service
    {
        private readonly SecretsModel _secretsManager;

        public S3Service(SecretsModel secretsManager)
        {
            _secretsManager = secretsManager;
        }

        public async Task<string> UploadToS3(IFormFile file)
        {
            var bucketName = _secretsManager.S3BucketName;
            var credentials = new BasicAWSCredentials(_secretsManager.S3WriteAccessKey, _secretsManager.S3WriteSecretKey);
            var config = new AmazonS3Config
            {
                RegionEndpoint = Amazon.RegionEndpoint.GetBySystemName(_secretsManager.S3BucketRegion)
            };
            using var client = new AmazonS3Client(credentials, config);

            var fileOnS3 = await GetFileInS3Async(client, bucketName, file);
            if (fileOnS3 != null)
            {
                return fileOnS3;
            }

            await using var newMemoryStream = new MemoryStream();
            file.CopyTo(newMemoryStream);

            var fileName = GetUniqueFileName(file.FileName);
            var uploadRequest = new TransferUtilityUploadRequest
            {
                InputStream = newMemoryStream,
                Key = fileName,
                BucketName = bucketName,
                CannedACL = S3CannedACL.PublicRead
            };

            var fileTransferUtility = new TransferUtility(client);
            await fileTransferUtility.UploadAsync(uploadRequest);
            return fileName;
        }

        private static string GetUniqueFileName(string originalFileName)
        {
            string fileName = Path.GetFileNameWithoutExtension(originalFileName);
            string fileExtension = Path.GetExtension(originalFileName);
            string currentDateTime = DateTime.Now.ToString("yyyyMMddHHmmss");

            fileName = fileName.Substring(0, Math.Min(fileName.Length, 15));

            return fileName + "_" + currentDateTime + fileExtension;
        }

        private static async Task<string> GetFileInS3Async(AmazonS3Client client, string bucketName, IFormFile file)
        {
            // Calculate the hash value of the file contents
            byte[] fileHash;
            using (var hashAlgorithm = SHA256.Create())
            {
                using var stream = file.OpenReadStream();
                fileHash = hashAlgorithm.ComputeHash(stream);
            }

            try
            {
                var listResponse = await client.ListObjectsV2Async(new Amazon.S3.Model.ListObjectsV2Request
                {
                    BucketName = bucketName
                });
                foreach (var s3Object in listResponse.S3Objects)
                {
                    var objectResponse = await client.GetObjectAsync(bucketName, s3Object.Key);
                    using var hashAlgorithm = SHA256.Create();
                    using var stream = objectResponse.ResponseStream;
                    var objectHash = hashAlgorithm.ComputeHash(stream);
                    if (fileHash.Length == objectHash.Length)
                    {
                        bool hashesMatch = true;
                        for (int i = 0; i < fileHash.Length; i++)
                        {
                            if (fileHash[i] != objectHash[i])
                            {
                                hashesMatch = false;
                                break;
                            }
                        }
                        if (hashesMatch)
                        {
                            return s3Object.Key;
                        }
                    }
                }

                return null;
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine("An AmazonS3Exception was thrown: " + ex.Message);
                return null;
            }
        }
    }
}
