
namespace DigiBrainServer.Models
{
    public class PathLearnQuizStatusModel
    {
        public long Id { get; set; }
        public string Username { get; set; }
        public long PathLearnId { get; set; }
        public long SectionNumber { get; set; }
        public long LessonNumber { get; set; }
        public long Score { get; set; }

        public PathLearnQuizStatusModel() { }

        public PathLearnQuizStatusModel(long Id, string Username, long PathLearnId, long SectionNumber, long LessonNumber, long Score)
        {
            this.Id = Id;
            this.PathLearnId = PathLearnId;
            this.Username = Username;
            this.SectionNumber = SectionNumber;
            this.LessonNumber = LessonNumber;
            this.Score = Score;
        }
    }
}
