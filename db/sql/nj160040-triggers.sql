CREATE TRIGGER TR_TransportOffer
-- ALTER TRIGGER TR_TransportOffer
   ON dbo.Package
   AFTER INSERT
AS
BEGIN
	DECLARE @idPackage INT
	DECLARE @type TINYINT
	DECLARE @weight FLOAT
	DECLARE @price FLOAT
	SET @idPackage = (SELECT TOP 1 idPackage FROM inserted ORDER BY idPackage DESC)
	SET @type = (SELECT type FROM inserted WHERE idPackage = @idPackage)
	SET @weight = (SELECT weight FROM inserted WHERE idPackage = @idPackage)
	IF @type = 0
		SET @price = 115
	ELSE IF @type = 1
		SET @price = 175 + 100 * @weight
	ELSE IF @type = 2
		SET @price = 250 + 100 * @weight
	ELSE
		SET @price = 350 + 500 * @weight
	UPDATE dbo.Package
	SET price = @price
	WHERE idPackage = @idPackage
END
