using DigiBrainServer.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Models
{
    public class QuizReportModel
    {
        public long Id { get; set; }
        public string Username { get; set; }
        public string QuizType { get; set; }
        public double Score { get; set; }
        public int NumberOfQuestions { get; set; }
        public long SubjectId { get; set; }
        public string Difficulty { get; set; }

        public QuizReportModel() { }

        public QuizReportModel(long Id, string Username, string QuizType, double Score, int NumberOfQuestions, long SubjectId, string Difficulty)
        {
            this.Id = Id;
            this.Username = Username;
            this.QuizType = QuizType;
            this.Score = Score;
            this.NumberOfQuestions = NumberOfQuestions;
            this.SubjectId = SubjectId;
            this.Difficulty = Difficulty;
        }

        public QuizReportModel(long Id, QuizReportViewModel model)
        {
            this.Id = Id;
            this.Username = model.Username;
            this.QuizType = model.QuizType;
            this.Score = model.Score;
            this.NumberOfQuestions = model.NumberOfQuestions;
            this.SubjectId = model.SubjectId;
            this.Difficulty = model.Difficulty;
        }
    }
}
