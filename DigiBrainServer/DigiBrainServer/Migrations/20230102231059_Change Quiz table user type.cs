using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class ChangeQuiztableusertype : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "UserId",
                table: "Quiz");

            migrationBuilder.AddColumn<string>(
                name: "Username",
                table: "Quiz",
                type: "text",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Username",
                table: "Quiz");

            migrationBuilder.AddColumn<long>(
                name: "UserId",
                table: "Quiz",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);
        }
    }
}
