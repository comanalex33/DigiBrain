using DigiBrainServer.ResponseModels;
using Newtonsoft.Json;

namespace DigiBrainServer.Models
{
    public class SubjectQuestionModel
    {
        public long Id { get; set; }
        public long SubjectId { get; set; }
        public long QuestionId { get; set; }
        public bool RequestToResolve { get; set; }

        public SubjectQuestionModel() { }
        public SubjectQuestionModel(long Id, long SubjectId, long QuestionId)
        {
            this.Id = Id;
            this.SubjectId = SubjectId;
            this.QuestionId = QuestionId;
            RequestToResolve = false;
        }

        public static explicit operator SubjectQuestionResponseModel(SubjectQuestionModel model)
        {
            return JsonConvert.DeserializeObject<SubjectQuestionResponseModel>(JsonConvert.SerializeObject(model));
        }
    }
}
