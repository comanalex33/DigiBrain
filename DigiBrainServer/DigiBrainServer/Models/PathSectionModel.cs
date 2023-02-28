using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class PathSectionModel
    {
        public long Id { get; set; }
        public long Number { get; set; }
        public string Title { get; set; }
        public long IconId { get; set; }
        public long PathLearnId { get; set; }
        public bool RequestToResolve { get; set; }

        public PathSectionModel() { }

        public PathSectionModel(long Id, long Number, string Title, long IconId, long PathLearnId)
        {
            this.Id = Id;
            this.Number = Number;
            this.Title = Title;
            this.IconId = IconId;
            this.PathLearnId = PathLearnId;
            RequestToResolve = false;
        }

        public static explicit operator PathSectionResponseModel(PathSectionModel model)
        {
            return JsonConvert.DeserializeObject<PathSectionResponseModel>(JsonConvert.SerializeObject(model));
        }
    }
}
