using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class LessonModel
    {
        public long Id { get; set; }
        public string Title { get; set; }
        public string Text { get; set; }
        public long ChapterId { get; set; }
        public bool RequestToResolve { get; set; }

        public LessonModel() { }

        public LessonModel(long Id, string Title, string Text, long ChapterId)
        {
            this.Id = Id;
            this.Title = Title;
            this.Text = Text;
            this.ChapterId = ChapterId;
            RequestToResolve = false;
        }

        public static explicit operator LessonResponseModel(LessonModel lesson)
        {
            return JsonConvert.DeserializeObject<LessonResponseModel>(JsonConvert.SerializeObject(lesson));
        }
    }
}
