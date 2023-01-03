using System;

namespace DigiBrainServer.Models
{
    public class QuizModel
    {
        public long Id { get; set; }
        public DateTime Date { get; set; }
        public string Username { get; set; }

        public QuizModel() { }
        public QuizModel(long Id, DateTime Date, string Username)
        {
            this.Id = Id;
            this.Date = Date;
            this.Username = Username;
        }
    }
}
