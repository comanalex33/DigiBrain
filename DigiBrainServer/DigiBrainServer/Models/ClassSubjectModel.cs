
namespace DigiBrainServer.Models
{
    public class ClassSubjectModel
    {
        public long Id { get; set; }
        public long ClassId { get; set; }
        public long SubjectId { get; set; }

        public ClassSubjectModel() { }

        public ClassSubjectModel(long Id, long ClassId, long SubjectId)
        {
            this.Id = Id;
            this.ClassId = ClassId;
            this.SubjectId = SubjectId;
        }
    }
}
