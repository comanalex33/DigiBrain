using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.ResponseModels
{
    public class QuestionsAnswersResponseModel
    {
        public long Id { get; set; }
        public string Type { get; set; }
        public string Difficulty { get; set; }
        public string Text { get; set; }
        public long LanguageId { get; set; }
        public List<AnswerResponseModel> Answers { get; set; }

        public QuestionsAnswersResponseModel(long Id, string Type, string Difficulty, string Text, long LanguageId, List<AnswerResponseModel> Answers)
        {
            this.Id = Id;
            this.Type = Type;
            this.Difficulty = Difficulty;
            this.Text = Text;
            this.LanguageId = LanguageId;
            this.Answers = Answers;
        }
    }
}
