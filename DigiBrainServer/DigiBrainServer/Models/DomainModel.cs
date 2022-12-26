using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class DomainModel
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public long LanguageId { get; set; }
        public long IconId { get; set; }
        public bool RequestToResolve { get; set; }

        public DomainModel() { }

        public DomainModel(long Id, string Name, long LanguageId, long IconId)
        {
            this.Id = Id;
            this.Name = Name;
            this.LanguageId = LanguageId;
            this.IconId = IconId;
            RequestToResolve = false;
        }

        public static explicit operator DomainResponseModel(DomainModel domain)
        {
            return JsonConvert.DeserializeObject<DomainResponseModel>(JsonConvert.SerializeObject(domain));
        }
    }
}
