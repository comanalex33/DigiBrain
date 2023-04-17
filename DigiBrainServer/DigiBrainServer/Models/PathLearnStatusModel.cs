
namespace DigiBrainServer.Models
{
    public class PathLearnStatusModel
    {
        public long Id { get; set; }
        public string Username { get; set; }
        public long PathLearnId { get; set; }
        public long SectionNumber { get; set; }
        public long LessonNumber { get; set; }
        public long TheoryNumber { get; set; }
        public bool Finished { get; set; }

        public PathLearnStatusModel() { }

        public PathLearnStatusModel(long Id, string Username, long PathLearnId, long SectionNumber, long LessonNumber, long TheoryNumber, bool Finished)
        {
            this.Id = Id;
            this.Username = Username;
            this.PathLearnId = PathLearnId;
            this.SectionNumber = SectionNumber;
            this.LessonNumber = LessonNumber;
            this.TheoryNumber = TheoryNumber;
            this.Finished = Finished;
        }
    }
}
