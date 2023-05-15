using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class UpdatedLearnPathstatustable : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_PathStatus",
                table: "PathStatus");

            migrationBuilder.RenameTable(
                name: "PathStatus",
                newName: "PathLearnStatus");

            migrationBuilder.RenameColumn(
                name: "SectionId",
                table: "PathLearnStatus",
                newName: "TheoryNumber");

            migrationBuilder.RenameColumn(
                name: "LessonId",
                table: "PathLearnStatus",
                newName: "SectionNumber");

            migrationBuilder.AddColumn<long>(
                name: "Started",
                table: "PathLearn",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);

            migrationBuilder.AddColumn<bool>(
                name: "Finished",
                table: "PathLearnStatus",
                type: "boolean",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<long>(
                name: "LessonNumber",
                table: "PathLearnStatus",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);

            migrationBuilder.AddPrimaryKey(
                name: "PK_PathLearnStatus",
                table: "PathLearnStatus",
                column: "Id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_PathLearnStatus",
                table: "PathLearnStatus");

            migrationBuilder.DropColumn(
                name: "Started",
                table: "PathLearn");

            migrationBuilder.DropColumn(
                name: "Finished",
                table: "PathLearnStatus");

            migrationBuilder.DropColumn(
                name: "LessonNumber",
                table: "PathLearnStatus");

            migrationBuilder.RenameTable(
                name: "PathLearnStatus",
                newName: "PathStatus");

            migrationBuilder.RenameColumn(
                name: "TheoryNumber",
                table: "PathStatus",
                newName: "SectionId");

            migrationBuilder.RenameColumn(
                name: "SectionNumber",
                table: "PathStatus",
                newName: "LessonId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_PathStatus",
                table: "PathStatus",
                column: "Id");
        }
    }
}
