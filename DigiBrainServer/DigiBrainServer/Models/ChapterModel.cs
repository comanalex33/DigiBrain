using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class ChapterModel
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public long Number { get; set; }
        public long SubjectId { get; set; }
        public bool RequestToResolve { get; set; }
        
        public ChapterModel() { }

        public ChapterModel(long Id, string Name, long Number, long SubjectId)
        {
            this.Id = Id;
            this.Name = Name;
            this.Number = Number;
            this.SubjectId = SubjectId;
            RequestToResolve = false;
        }

        public static explicit operator ChapterResponseModel(ChapterModel chapter)
        {
            return JsonConvert.DeserializeObject<ChapterResponseModel>(JsonConvert.SerializeObject(chapter));
        }
    }
}
