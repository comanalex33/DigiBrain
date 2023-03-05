using System;
using System.Collections.Generic;

namespace DigiBrainServer.ResponseModels
{
    public class PathLearnDetailsResponse
    {
        public long Id { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public string Author { get; set; }
        public DateTime Date { get; set; }
        public long SubjectId { get; set; }
        public string ImageName { get; set; }
        
        public List<PathLearnSectionDetailsResponse> Sections { get; set; }

        public PathLearnDetailsResponse(PathLearnResponseModel model, List<PathLearnSectionDetailsResponse> Sections)
        {
            Id = model.Id;
            Title = model.Title;
            Description = model.Description;
            Author = model.Author;
            Date = model.Date;
            SubjectId = model.SubjectId;
            ImageName = model.ImageName;
            this.Sections = Sections;
        }
    }

    public class PathLearnSectionDetailsResponse
    {
        public long Id { get; set; }
        public long Number { get; set; }
        public string Title { get; set; }
        public long IconId { get; set; }
        public long PathLearnId { get; set; }

        public List<PathLearnLessonDetailsResponse> Lessons { get; set; }

        public PathLearnSectionDetailsResponse(PathSectionResponseModel model, List<PathLearnLessonDetailsResponse> Lessons)
        {
            Id = model.Id;
            Number = model.Number;
            Title = model.Title;
            IconId = model.IconId;
            PathLearnId = model.PathLearnId;
            this.Lessons = Lessons;
        }
    }

    public class PathLearnLessonDetailsResponse
    {
        public long Id { get; set; }
        public long Number { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public long PathSectionId { get; set; }

        public List<PathLessonResponseQuiz> Quiz { get; set; }
        public List<PathLessonResponseTheory> Theory { get; set; }

        public PathLearnLessonDetailsResponse(PathLessonResponseModel model, List<PathLessonResponseQuiz> Quiz, List<PathLessonResponseTheory> Theory)
        {
            Id = model.Id;
            Number = model.Number;
            Title = model.Title;
            Description = model.Description;
            PathSectionId = model.PathSectionId;
            this.Quiz = Quiz;
            this.Theory = Theory;
        }
    }
}
