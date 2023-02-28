using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class PathLessonTheory
    {
        public long Id { get; set; }
        public long Number { get; set; }
        public string Title { get; set; }
        public string Text { get; set; }
        public long PathLessonId { get; set; }
        public bool RequestToResolve { get; set; }

        public PathLessonTheory() { }

        public PathLessonTheory(long Id, long Number, string Title, string Text, long PathLessonId)
        {
            this.Id = Id;
            this.Number = Number;
            this.Title = Title;
            this.Text = Text;
            this.PathLessonId = PathLessonId;
            RequestToResolve = false;
        }

        public static explicit operator PathLessonResponseTheory(PathLessonTheory model)
        {
            return JsonConvert.DeserializeObject<PathLessonResponseTheory>(JsonConvert.SerializeObject(model));
        }
    }
}
