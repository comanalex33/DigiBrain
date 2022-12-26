using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace DigiBrainServer.Models
{
    public class AppDbContext: IdentityDbContext<UserModel, RoleModel, string>
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<LanguageModel> Language { get; set; }

        public DbSet<DomainModel> Domain { get; set; }

        public DbSet<ClassModel> Class { get; set; }

        public DbSet<SubjectModel> Subject { get; set; }

        public DbSet<ChapterModel> Chapter { get; set; }

        public DbSet<LessonModel> Lesson { get; set; }
    }
}
