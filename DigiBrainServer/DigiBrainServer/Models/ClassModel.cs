using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class ClassModel
    {
        public long Id { get; set; }
        public int Number { get; set; }
        public long DomainId { get; set; }
        public bool AtUniversity { get; set; }
        public bool RequestToResolve { get; set; }

        public ClassModel() { }
        public ClassModel(long Id, int Number, long DomainId, bool AtUniversity)
        {
            this.Id = Id;
            this.Number = Number;
            this.DomainId = DomainId;
            this.AtUniversity = AtUniversity;
            RequestToResolve = false;
        }

        public static explicit operator ClassResponseModel(ClassModel classModel)
        {
            return JsonConvert.DeserializeObject<ClassResponseModel>(JsonConvert.SerializeObject(classModel));
        }
    }
}
