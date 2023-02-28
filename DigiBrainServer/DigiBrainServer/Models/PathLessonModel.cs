using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class PathLessonModel
    {
        public long Id { get; set; }
        public long Number { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public long PathSectionId { get; set; }
        public bool RequestToResolve { get; set; }

        public PathLessonModel() { }

        public PathLessonModel(long Id, long Number, string Title, string Description, long PathSectionId)
        {
            this.Id = Id;
            this.Number = Number;
            this.Title = Title;
            this.Description = Description;
            this.PathSectionId = PathSectionId;
            RequestToResolve = false;
        }

        public static explicit operator PathLessonResponseModel(PathLessonModel model)
        {
            return JsonConvert.DeserializeObject<PathLessonResponseModel>(JsonConvert.SerializeObject(model));
        }
    }
}
