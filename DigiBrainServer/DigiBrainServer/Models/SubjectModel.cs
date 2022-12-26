using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class SubjectModel
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public long IconId { get; set; }
        public long ClassId { get; set; }
        public bool RequestToResolve { get; set; }

        public SubjectModel() { }

        public SubjectModel(long Id, string Name, long ClassId, long IconId)
        {
            this.Id = Id;
            this.Name = Name;
            this.ClassId = ClassId;
            this.IconId = IconId;
            RequestToResolve = false;
        }

        public static explicit operator SubjectResponseModel(SubjectModel subject)
        {
            return JsonConvert.DeserializeObject<SubjectResponseModel>(JsonConvert.SerializeObject(subject));
        }
    }
}
