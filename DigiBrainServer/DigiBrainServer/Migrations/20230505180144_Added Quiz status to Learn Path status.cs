using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

namespace DigiBrainServer.Migrations
{
    public partial class AddedQuizstatustoLearnPathstatus : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "Number",
                table: "PathLessonQuiz",
                newName: "Score");

            migrationBuilder.CreateTable(
                name: "PathLearnQuizStatusModel",
                columns: table => new
                {
                    Id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    SectionNumber = table.Column<long>(type: "bigint", nullable: false),
                    LessonNumber = table.Column<long>(type: "bigint", nullable: false),
                    Score = table.Column<long>(type: "bigint", nullable: false),
                    PathLearnStatusModelId = table.Column<long>(type: "bigint", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PathLearnQuizStatusModel", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PathLearnQuizStatusModel_PathLearnStatus_PathLearnStatusMod~",
                        column: x => x.PathLearnStatusModelId,
                        principalTable: "PathLearnStatus",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_PathLearnQuizStatusModel_PathLearnStatusModelId",
                table: "PathLearnQuizStatusModel",
                column: "PathLearnStatusModelId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "PathLearnQuizStatusModel");

            migrationBuilder.RenameColumn(
                name: "Score",
                table: "PathLessonQuiz",
                newName: "Number");
        }
    }
}
