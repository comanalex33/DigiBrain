using DigiBrainServer.ResponseModels;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class QuestionAnswersViewModel
    {
        [Required(ErrorMessage = "Difficulty required")]
        public string Text { get; set; }

        [Required(ErrorMessage = "Difficulty required")]
        public string Difficulty { get; set; }

        [Required(ErrorMessage = "Type required")]
        public string Type { get; set; }

        [Required(ErrorMessage = "Language required")]
        public long LanguageId { get; set; }

        public List<AnswerResponseModel> Answers { get; set; }
    }
}
