using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class QuestionGeneratorViewModel
    {
        [Required(ErrorMessage = "Number of questions required")]
        public int Number { get; set; }

        [Required(ErrorMessage = "Difficulty required")]
        public string Difficulty { get; set; }

        [Required(ErrorMessage = "Type required")]
        public string Type { get; set; }

        [Required(ErrorMessage = "Language required")]
        public long LanguageId { get; set; }
    }
}
