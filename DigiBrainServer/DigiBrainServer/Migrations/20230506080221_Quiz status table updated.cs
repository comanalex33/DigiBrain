using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class Quizstatustableupdated : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "StatusId",
                table: "PathLearnQuizStatus",
                newName: "PathLearnId");

            migrationBuilder.AddColumn<string>(
                name: "Username",
                table: "PathLearnQuizStatus",
                type: "text",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Username",
                table: "PathLearnQuizStatus");

            migrationBuilder.RenameColumn(
                name: "PathLearnId",
                table: "PathLearnQuizStatus",
                newName: "StatusId");
        }
    }
}
