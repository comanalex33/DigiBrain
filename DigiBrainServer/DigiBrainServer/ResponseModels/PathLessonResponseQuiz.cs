﻿
namespace DigiBrainServer.ResponseModels
{
    public class PathLessonResponseQuiz
    {
        public long Id { get; set; }
        public long Score { get; set; }
        public long QuestionId { get; set; }
        public long PathLessonId { get; set; }
    }
}
