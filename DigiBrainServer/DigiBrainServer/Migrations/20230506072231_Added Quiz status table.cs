using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class AddedQuizstatustable : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_PathLearnQuizStatusModel_PathLearnStatus_PathLearnStatusMod~",
                table: "PathLearnQuizStatusModel");

            migrationBuilder.DropPrimaryKey(
                name: "PK_PathLearnQuizStatusModel",
                table: "PathLearnQuizStatusModel");

            migrationBuilder.DropIndex(
                name: "IX_PathLearnQuizStatusModel_PathLearnStatusModelId",
                table: "PathLearnQuizStatusModel");

            migrationBuilder.DropColumn(
                name: "PathLearnStatusModelId",
                table: "PathLearnQuizStatusModel");

            migrationBuilder.RenameTable(
                name: "PathLearnQuizStatusModel",
                newName: "PathLearnQuizStatus");

            migrationBuilder.AddColumn<long>(
                name: "StatusId",
                table: "PathLearnQuizStatus",
                type: "bigint",
                nullable: false,
                defaultValue: 0L);

            migrationBuilder.AddPrimaryKey(
                name: "PK_PathLearnQuizStatus",
                table: "PathLearnQuizStatus",
                column: "Id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_PathLearnQuizStatus",
                table: "PathLearnQuizStatus");

            migrationBuilder.DropColumn(
                name: "StatusId",
                table: "PathLearnQuizStatus");

            migrationBuilder.RenameTable(
                name: "PathLearnQuizStatus",
                newName: "PathLearnQuizStatusModel");

            migrationBuilder.AddColumn<long>(
                name: "PathLearnStatusModelId",
                table: "PathLearnQuizStatusModel",
                type: "bigint",
                nullable: true);

            migrationBuilder.AddPrimaryKey(
                name: "PK_PathLearnQuizStatusModel",
                table: "PathLearnQuizStatusModel",
                column: "Id");

            migrationBuilder.CreateIndex(
                name: "IX_PathLearnQuizStatusModel_PathLearnStatusModelId",
                table: "PathLearnQuizStatusModel",
                column: "PathLearnStatusModelId");

            migrationBuilder.AddForeignKey(
                name: "FK_PathLearnQuizStatusModel_PathLearnStatus_PathLearnStatusMod~",
                table: "PathLearnQuizStatusModel",
                column: "PathLearnStatusModelId",
                principalTable: "PathLearnStatus",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }
    }
}
