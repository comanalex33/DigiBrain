
namespace DigiBrainServer.Models
{
    public class PathLearnStatusModel
    {
        public long Id { get; set; }
        public string Username { get; set; }
        public long PathLearnId { get; set; }
        public long SectionId { get; set; }
        public long LessonId { get; set; }

        public PathLearnStatusModel() { }

        public PathLearnStatusModel(long Id, string Username, long PathLearnId, long SectionId, long LessonId)
        {
            this.Id = Id;
            this.Username = Username;
            this.PathLearnId = PathLearnId;
            this.SectionId = SectionId;
            this.LessonId = LessonId;
        }
    }
}
