using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class RevertClassfields : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "LanguageId",
                table: "Class");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<long>(
                name: "LanguageId",
                table: "Class",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);
        }
    }
}
