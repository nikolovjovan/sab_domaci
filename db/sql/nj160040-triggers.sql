CREATE TRIGGER TR_TransportOffer
--ALTER TRIGGER TR_TransportOffer
   ON dbo.Package
   AFTER INSERT, UPDATE
AS
BEGIN
	DECLARE @idPackage INT
	DECLARE @type TINYINT
	DECLARE @weight FLOAT
	DECLARE @idAddressFrom INT
	DECLARE @idAddressTo INT
	DECLARE @xCordFrom INT
	DECLARE @yCordFrom INT
	DECLARE @xCordTo INT
	DECLARE @yCordTo INT
	DECLARE @price FLOAT
	SELECT TOP 1 @idPackage = idPackage, @type = type, @weight = weight,
	             @idAddressFrom = idAddressFrom, @idAddressTo = idAddressTo
	FROM inserted ORDER BY idPackage DESC
	--PRINT @idPackage
	--PRINT @type
	--PRINT @weight
	--PRINT @idAddressFrom
	--PRINT @idAddressTo

	SELECT @xCordFrom = xCord, @yCordFrom = yCord FROM Address WHERE idAddress = @idAddressFrom
	--PRINT @xCordFrom
	--PRINT @yCordFrom

	SELECT @xCordTo = xCord, @yCordTo = yCord FROM Address WHERE idAddress = @idAddressTo
	--PRINT @xCordTo
	--PRINT @yCordTo

	IF @type = 0
		SET @price = 115
	ELSE IF @type = 1
		SET @price = 175 + 100 * @weight
	ELSE IF @type = 2
		SET @price = 250 + 100 * @weight
	ELSE
		SET @price = 350 + 500 * @weight
	--PRINT @price

	--PRINT SQRT(SQUARE(@xCordTo - @xCordFrom) + SQUARE(@yCordTo - @yCordFrom))

	SET @price *= SQRT(SQUARE(@xCordTo - @xCordFrom) + SQUARE(@yCordTo - @yCordFrom))
	UPDATE dbo.Package SET price = @price WHERE idPackage = @idPackage
END
