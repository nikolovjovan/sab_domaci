USE [nj160040]

CREATE TABLE [City]
( 
	[idCity]             integer  IDENTITY ( 0,1 )  NOT NULL ,
	[name]               varchar(100)  NOT NULL ,
	[postalCode]         varchar(100)  NOT NULL ,
	CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([idCity] ASC)
)
go

CREATE TABLE [Address]
( 
	[idAddress]          integer  IDENTITY ( 0,1 )  NOT NULL ,
	[street]             varchar(100)  NOT NULL ,
	[number]             integer  NOT NULL ,
	[xCord]              integer  NOT NULL ,
	[yCord]              integer  NOT NULL ,
	[idCity]             integer  NOT NULL ,
	CONSTRAINT [XPKAddress] PRIMARY KEY  CLUSTERED ([idAddress] ASC),
	CONSTRAINT [AddressCity] FOREIGN KEY ([idCity]) REFERENCES [City]([idCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [User]
( 
	[userName]           varchar(100)  NOT NULL ,
	[password]           varchar(100)  NOT NULL ,
	[firstName]          varchar(100)  NOT NULL ,
	[lastName]           varchar(100)  NOT NULL ,
	[idAddress]          integer  NOT NULL ,
	[type]               tinyint  NOT NULL 
	CONSTRAINT [UserDefaultType]
		 DEFAULT  0
	CONSTRAINT [UserType]
		CHECK  ( type in (0, 1, 2, 3) ),
	CONSTRAINT [XPKUser] PRIMARY KEY  CLUSTERED ([userName] ASC),
	CONSTRAINT [UserAddress] FOREIGN KEY ([idAddress]) REFERENCES [Address]([idAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [Courier]
( 
	[userName]           varchar(100)  NOT NULL ,
	[driversLicenseNumber] varchar(100)  NOT NULL ,
	[status]             tinyint  NOT NULL 
	CONSTRAINT [CourierStatus]
		CHECK  ( status in (0, 1, 2) ),
	[profit]             decimal(10,3)  NULL 
	CONSTRAINT [CourierDefaultProfit]
		 DEFAULT  0,
	CONSTRAINT [XPKCourier] PRIMARY KEY  CLUSTERED ([userName] ASC),
	CONSTRAINT [CourierUser] FOREIGN KEY ([userName]) REFERENCES [User]([userName])
		ON DELETE CASCADE
		ON UPDATE CASCADE
)
go

CREATE TABLE [Stop]
( 
	[userName]           varchar(100)  NOT NULL ,
	[idAddress]          integer  NOT NULL ,
	[stopNumber]         integer  NOT NULL ,
	CONSTRAINT [XPKStop] PRIMARY KEY  CLUSTERED ([userName] ASC,[idAddress] ASC,[stopNumber] ASC),
	CONSTRAINT [StopAddress] FOREIGN KEY ([idAddress]) REFERENCES [Address]([idAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [Driver] FOREIGN KEY ([userName]) REFERENCES [Courier]([userName])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [Package]
( 
	[idPackage]          integer  IDENTITY ( 0,1 )  NOT NULL ,
	[type]               tinyint  NOT NULL 
	CONSTRAINT [PackageType]
		CHECK  ( type in (0, 1, 2, 3) ),
	[weight]             decimal(10,3)  NOT NULL ,
	[price]              decimal(10,3)  NULL ,
	[createTime]         datetime  NULL ,
	[acceptTime]         datetime  NULL ,
	[idAddressFrom]      integer  NOT NULL ,
	[idAddressTo]        integer  NOT NULL ,
	[status]             integer  NULL 
	CONSTRAINT [PackageDefaultStatus]
		 DEFAULT  0
	CONSTRAINT [PackageStatus]
		CHECK  ( status in (0, 1, 2, 3, 4) ),
	[senderUserName]     varchar(100)  NOT NULL ,
	[courierUserName]    varchar(100)  NULL ,
	[idAddress]          integer  NULL ,
	CONSTRAINT [XPKPackage] PRIMARY KEY  CLUSTERED ([idPackage] ASC),
	CONSTRAINT [PackageAddressFrom] FOREIGN KEY ([idAddressFrom]) REFERENCES [Address]([idAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [PackageAddressTo] FOREIGN KEY ([idAddressTo]) REFERENCES [Address]([idAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [PackageOwner] FOREIGN KEY ([senderUserName]) REFERENCES [User]([userName])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [PackageLocation] FOREIGN KEY ([idAddress]) REFERENCES [Address]([idAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [PackageCourier] FOREIGN KEY ([courierUserName]) REFERENCES [User]([userName])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [IsPickingUp]
( 
	[idPackage]          integer  NOT NULL ,
	[userName]           varchar(100)  NOT NULL ,
	[stopNumber]         integer  NOT NULL ,
	CONSTRAINT [XPKIsPickingUp] PRIMARY KEY  CLUSTERED ([idPackage] ASC,[userName] ASC),
	CONSTRAINT [PackageIsPickedUpBy] FOREIGN KEY ([idPackage]) REFERENCES [Package]([idPackage])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [CourierIsPickingUp] FOREIGN KEY ([userName]) REFERENCES [Courier]([userName])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [Stockroom]
( 
	[idStockroom]        integer  IDENTITY ( 0,1 )  NOT NULL ,
	[idAddress]          integer  NOT NULL ,
	CONSTRAINT [XPKStockroom] PRIMARY KEY  CLUSTERED ([idStockroom] ASC),
	CONSTRAINT [StockroomAddress] FOREIGN KEY ([idAddress]) REFERENCES [Address]([idAddress])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [Vehicle]
( 
	[licensePlateNumber] varchar(100)  NOT NULL ,
	[capacity]           decimal(10,3)  NOT NULL ,
	[fuelConsumption]    decimal(10,3)  NOT NULL ,
	[fuelType]           tinyint  NOT NULL 
	CONSTRAINT [VehicleFuelType]
		CHECK  ( fuelType in (0, 1, 2) ),
	[idStockroom]        integer  NULL ,
	CONSTRAINT [XPKVehicle] PRIMARY KEY  CLUSTERED ([licensePlateNumber] ASC),
	CONSTRAINT [VehicleStartingStockroom] FOREIGN KEY ([idStockroom]) REFERENCES [Stockroom]([idStockroom])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [IsDriving]
( 
	[userName]           varchar(100)  NOT NULL 
	CONSTRAINT [IsDrivingDefaultDistanceTraveled_1998794399]
		 DEFAULT  0,
	[licensePlateNumber] varchar(100)  NOT NULL ,
	[distanceTraveled]   decimal(10,3)  NOT NULL 
	CONSTRAINT [IsDrivingDefaultDistanceTraveled]
		 DEFAULT  0,
	[currentStopNumber]  integer  NOT NULL ,
	[remainingCapacity]  decimal(10,3)  NOT NULL ,
	CONSTRAINT [XPKIsDriving] PRIMARY KEY  CLUSTERED ([userName] ASC,[licensePlateNumber] ASC),
	CONSTRAINT [VehicleIsBeingDrivenBy] FOREIGN KEY ([licensePlateNumber]) REFERENCES [Vehicle]([licensePlateNumber])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [CourierIsDriving] FOREIGN KEY ([userName]) REFERENCES [Courier]([userName])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [IsDelivering]
( 
	[idPackage]          integer  NOT NULL ,
	[userName]           varchar(100)  NOT NULL ,
	[stopNumber]         integer  NOT NULL ,
	CONSTRAINT [XPKIsDelivering] PRIMARY KEY  CLUSTERED ([idPackage] ASC,[userName] ASC),
	CONSTRAINT [PackageIsDeliveredBy] FOREIGN KEY ([idPackage]) REFERENCES [Package]([idPackage])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [CourierIsDelivering] FOREIGN KEY ([userName]) REFERENCES [Courier]([userName])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TABLE [Drove]
( 
	[userName]           varchar(100)  NOT NULL ,
	[licensePlateNumber] varchar(100)  NOT NULL ,
	CONSTRAINT [XPKDrove] PRIMARY KEY  CLUSTERED ([userName] ASC,[licensePlateNumber] ASC),
	CONSTRAINT [VehicleWasDrivenBy] FOREIGN KEY ([licensePlateNumber]) REFERENCES [Vehicle]([licensePlateNumber])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT [CourierDrove] FOREIGN KEY ([userName]) REFERENCES [Courier]([userName])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
)
go

CREATE TRIGGER TR_TransportOffer
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
	SELECT @xCordFrom = xCord, @yCordFrom = yCord FROM Address WHERE idAddress = @idAddressFrom
	SELECT @xCordTo = xCord, @yCordTo = yCord FROM Address WHERE idAddress = @idAddressTo

	IF @type = 0
		SET @price = 115
	ELSE IF @type = 1
		SET @price = 175 + 100 * @weight
	ELSE IF @type = 2
		SET @price = 250 + 100 * @weight
	ELSE
		SET @price = 350 + 500 * @weight
	SET @price *= SQRT(SQUARE(@xCordTo - @xCordFrom) + SQUARE(@yCordTo - @yCordFrom))

	UPDATE dbo.Package SET price = @price WHERE idPackage = @idPackage
END;

ENABLE TRIGGER [TR_TransportOffer] ON Package
go
