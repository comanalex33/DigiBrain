using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class LessonQuestionModel
    {
        public long Id { get; set; }
        public long LessonId { get; set; }
        public long QuestionId { get; set; }
        public bool RequestToResolve { get; set; }

        public LessonQuestionModel() { }
        public LessonQuestionModel(long Id, long LessonId, long QuestionId)
        {
            this.Id = Id;
            this.LessonId = LessonId;
            this.QuestionId = QuestionId;
            RequestToResolve = false;
        }

        public static explicit operator LessonQuestionResponseModel(LessonQuestionModel model)
        {
            return JsonConvert.DeserializeObject<LessonQuestionResponseModel>(JsonConvert.SerializeObject(model));
        }
    }
}
