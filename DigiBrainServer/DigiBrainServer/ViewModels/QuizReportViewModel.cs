using System.ComponentModel.DataAnnotations;

namespace DigiBrainServer.ViewModels
{
    public class QuizReportViewModel
    {
        [Required(ErrorMessage = "Username required")]
        public string Username { get; set; }

        [Required(ErrorMessage = "Quiz type required")]
        public string QuizType { get; set; }

        [Required(ErrorMessage = "Score required")]
        public double Score { get; set; }

        [Required(ErrorMessage = "number of questions required")]
        public int NumberOfQuestions { get; set; }

        [Required(ErrorMessage = "Subject required")]
        public long SubjectId { get; set; }

        [Required(ErrorMessage = "Difficulty required")]
        public string Difficulty { get; set; }
    }
}
