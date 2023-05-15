using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace DigiBrainServer.Models
{
    public class AppDbContext: IdentityDbContext<UserModel, RoleModel, string>
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        // Learn models
        public DbSet<LanguageModel> Language { get; set; }
        public DbSet<DomainModel> Domain { get; set; }
        public DbSet<ClassModel> Class { get; set; }
        public DbSet<SubjectModel> Subject { get; set; }
        public DbSet<ChapterModel> Chapter { get; set; }
        public DbSet<LessonModel> Lesson { get; set; }
        
        // Quiz models
        public DbSet<QuestionModel> Question { get; set; }
        public DbSet<AnswerModel> Answer { get; set; }
        public DbSet<SubjectQuestionModel> LessonQuestion { get; set; }
        public DbSet<QuizModel> Quiz { get; set; }
        public DbSet<QuizQuestionModel> QuizQuestion { get; set; }
        public DbSet<QuizReportModel> QuizReport { get; set; }

        // Learning Path models
        public DbSet<PathLearnModel> PathLearn { get; set; }
        public DbSet<PathSectionModel> PathSection { get; set; }
        public DbSet<PathLessonModel> PathLesson { get; set; }
        public DbSet<PathLessonTheory> PathLessonTheory { get; set; }
        public DbSet<PathLessonQuiz> PathLessonQuiz { get; set; }
        public DbSet<PathLearnStatusModel> PathLearnStatus { get; set; }
    }
}
