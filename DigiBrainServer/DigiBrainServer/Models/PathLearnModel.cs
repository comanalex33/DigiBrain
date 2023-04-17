using System;
using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class PathLearnModel
    {
        public long Id { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public string Author { get; set; }
        public DateTime Date { get; set; }
        public long Started { get; set; }
        public long SubjectId { get; set; }
        public string ImageName { get; set; }
        public bool RequestToResolve { get; set; }

        public PathLearnModel() { }

        public PathLearnModel(long Id, string Title, string Description, string Author, DateTime Date, long Started, long SubjectId, string ImageName)
        {
            this.Id = Id;
            this.Title = Title;
            this.Description = Description;
            this.Author = Author;
            this.Date = Date;
            this.Started = Started;
            this.SubjectId = SubjectId;
            this.ImageName = ImageName;
            RequestToResolve = false;
        }

        public static explicit operator PathLearnResponseModel(PathLearnModel model)
        {
            return JsonConvert.DeserializeObject<PathLearnResponseModel>(JsonConvert.SerializeObject(model));
        }
    }
}
