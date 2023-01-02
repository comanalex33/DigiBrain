using System;

namespace DigiBrainServer.Models
{
    public class QuizModel
    {
        public long Id { get; set; }
        public DateTime Date { get; set; }
        public long UserId { get; set; }

        public QuizModel() { }
        public QuizModel(long Id, DateTime Date, long UserId)
        {
            this.Id = Id;
            this.Date = Date;
            this.UserId = UserId;
        }
    }
}
