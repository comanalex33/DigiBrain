using DigiBrainServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.ResponseModels
{
    public class PathLearnStatusResponseModel
    {
        public long Id { get; set; }
        public string Username { get; set; }
        public long PathLearnId { get; set; }
        public long SectionNumber { get; set; }
        public long LessonNumber { get; set; }
        public long TheoryNumber { get; set; }
        public bool Finished { get; set; }
        public List<PathLearnQuizStatusModel> Quiz { get; set; }

        public PathLearnStatusResponseModel(PathLearnStatusModel model)
        {
            Id = model.Id;
            Username = model.Username;
            PathLearnId = model.PathLearnId;
            SectionNumber = model.SectionNumber;
            LessonNumber = model.LessonNumber;
            TheoryNumber = model.TheoryNumber;
            Finished = model.Finished;
            Quiz = new List<PathLearnQuizStatusModel>();
        }

        public void AddQuiz(PathLearnQuizStatusModel model)
        {
            Quiz.Add(model);
        }
    }
}
