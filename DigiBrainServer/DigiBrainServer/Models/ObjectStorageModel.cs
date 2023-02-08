using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Models
{
    public class ObjectStorageModel
    {
        public string ReadAccessKey { get; set; }
        public string ReadSecretKey { get; set; }
        public string BucketName { get; set; }
        public string BucketRegion { get; set; }

        public ObjectStorageModel() { }

        public ObjectStorageModel(string ReadAccessKey, string ReadSecretKey, string BucketName, string BucketRegion)
        {
            this.ReadAccessKey = ReadAccessKey;
            this.ReadSecretKey = ReadSecretKey;
            this.BucketName = BucketName;
            this.BucketRegion = BucketRegion;
        }
    }
}
