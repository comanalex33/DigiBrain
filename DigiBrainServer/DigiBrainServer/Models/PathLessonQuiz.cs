using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class PathLessonQuiz
    {
        public long Id { get; set; }
        public long Number { get; set; }
        public long QuestionId { get; set; }
        public long PathLessonId { get; set; }
        public bool RequestToResolve { get; set; }

        public PathLessonQuiz() { }

        public PathLessonQuiz(long Id, long Number, long QuestionId, long PathLessonId)
        {
            this.Id = Id;
            this.Number = Number;
            this.QuestionId = QuestionId;
            this.PathLessonId = PathLessonId;
            RequestToResolve = false;
        }

        public static explicit operator PathLessonResponseQuiz(PathLessonQuiz model)
        {
            return JsonConvert.DeserializeObject<PathLessonResponseQuiz>(JsonConvert.SerializeObject(model));
        }
    }
}
