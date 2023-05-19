using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class PathLessonQuiz
    {
        public long Id { get; set; }
        public long Score { get; set; }
        public long QuestionId { get; set; }
        public long PathLessonId { get; set; }
        public bool RequestToResolve { get; set; }

        public PathLessonQuiz() { }

        public PathLessonQuiz(long Id, long Score, long QuestionId, long PathLessonId)
        {
            this.Id = Id;
            this.Score = Score;
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
