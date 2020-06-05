USE [nj160040]

DELETE FROM [dbo].[Stop]
DELETE FROM [dbo].[IsDelivering]
DELETE FROM [dbo].[IsPickingUp]
DELETE FROM [dbo].[IsDriving]
DELETE FROM [dbo].[Drove]
DELETE FROM [dbo].[Vehicle]
DELETE FROM [dbo].[Package]
DELETE FROM [dbo].[Courier]
DELETE FROM [dbo].[User]
DELETE FROM [dbo].[Stockroom]
DELETE FROM [dbo].[Address]
DELETE FROM [dbo].[City]

DBCC CHECKIDENT ('[Package]', RESEED, 0)
DBCC CHECKIDENT ('[Stockroom]', RESEED, 0)
DBCC CHECKIDENT ('[Address]', RESEED, 0)
DBCC CHECKIDENT ('[City]', RESEED, 0)

--insert City
INSERT INTO [dbo].[City] ([name], [postalCode]) VALUES ('Belgrade', '11000')
INSERT INTO [dbo].[City] ([name], [postalCode]) VALUES ('Kragujevac', '34000')
INSERT INTO [dbo].[City] ([name], [postalCode]) VALUES ('Valjevo', '14000')
INSERT INTO [dbo].[City] ([name], [postalCode]) VALUES ('Cacak', '32000')
INSERT INTO [dbo].[City] ([name], [postalCode]) VALUES ('Kraljevo', '36000')
INSERT INTO [dbo].[City] ([name], [postalCode]) VALUES ('Nis', '18000')
INSERT INTO [dbo].[City] ([name], [postalCode]) VALUES ('Novi Sad', '21000')

--insert Address
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (1, 'Kraljice Natalije', 37, 11, 15)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (1, 'Bulevar Kralja Aleksandra', 73, 10, 10)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (1, 'Vojvode Stepe', 39, 1, -1)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (1, 'Takovska', 7, 11, 12)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (1, 'Bulevar Kralja Aleksandra', 37, 10, 10)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (2, 'Daniciceva', 1, 4, 310)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (2, 'Dure Pucara Starog', 2, 11, 32)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (3, 'Cika Ljubina', 8, 102, 101)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (4, 'Rostiljska', 69, 420, 69)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (4, 'Pasuljska', 12, 418, 63)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (5, 'Dositejeva', 25, 6, 350)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (5, 'Karadjordjeva', 59, 10, 349)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (6, 'Ulica Dzeza', 23, 89, 578)
INSERT INTO [dbo].[Address] ([idCity], [street], [number], [xCord], [yCord]) VALUES (7, 'Dunavska', 26, 0, 0)

--insert User
INSERT INTO [dbo].[User] ([userName], [password], [firstName], [lastName], [idAddress], [type]) VALUES ('pera', 'velikiPera214_', 'Petar', 'Peric', 2, 1)
INSERT INTO [dbo].[User] ([userName], [password], [firstName], [lastName], [idAddress], [type]) VALUES ('zika', 'Ziki531$', 'Zivorad', 'Zivanovic', 7, 2)
INSERT INTO [dbo].[User] ([userName], [password], [firstName], [lastName], [idAddress], [type]) VALUES ('rada', 'Radojkaaa$876', 'Radojka', 'Radovanovic', 10, 0)
INSERT INTO [dbo].[User] ([userName], [password], [firstName], [lastName], [idAddress], [type]) VALUES ('mica', 'Trofrtaljka123@', 'Milica', 'Milicevic', 13, 3)

--insert Courier
INSERT INTO [dbo].[Courier] ([userName], [driversLicenseNumber], [status], [profit]) VALUES ('pera', '999222999', 1, 234983.298)
INSERT INTO [dbo].[Courier] ([userName], [driversLicenseNumber], [status], [profit]) VALUES ('mica', '789219800', 0, 9879489.597)
INSERT INTO [dbo].[Courier] ([userName], [driversLicenseNumber], [status], [profit]) VALUES ('rada', '116897821', 2, 0.000)

--insert Stop
INSERT INTO [dbo].[Stop] ([userName], [idAddress], [stopNumber]) VALUES ('pera', 1, 0)
INSERT INTO [dbo].[Stop] ([userName], [idAddress], [stopNumber]) VALUES ('pera', 4, 1)
INSERT INTO [dbo].[Stop] ([userName], [idAddress], [stopNumber]) VALUES ('pera', 5, 2)
INSERT INTO [dbo].[Stop] ([userName], [idAddress], [stopNumber]) VALUES ('pera', 11, 3)
INSERT INTO [dbo].[Stop] ([userName], [idAddress], [stopNumber]) VALUES ('pera', 12, 4)
INSERT INTO [dbo].[Stop] ([userName], [idAddress], [stopNumber]) VALUES ('pera', 1, 5)

--insert Package
INSERT INTO [dbo].[Package] ([type], [weight], [price], [createTime], [acceptTime], [idAddressFrom], [idAddressTo], [status], [senderUserName], [courierUserName], [idAddress]) VALUES (1, 12.3, 53413.123, '20200605 10:34:09 AM', '20200606 7:21:53 PM', 2, 7, 3, 'zika', 'mica', 7)
INSERT INTO [dbo].[Package] ([type], [weight], [price], [createTime], [acceptTime], [idAddressFrom], [idAddressTo], [status], [senderUserName], [courierUserName], [idAddress]) VALUES (3, 2.59, 78926.248, '20200605 10:34:09 AM', '20200606 7:21:53 PM', 5, 11, 3, 'zika', 'mica', 11)
INSERT INTO [dbo].[Package] ([type], [weight], [price], [createTime], [acceptTime], [idAddressFrom], [idAddressTo], [status], [senderUserName], [courierUserName], [idAddress]) VALUES (0, 0.212, 1230.213, '20200605 10:34:09 AM', '20200606 7:21:53 PM', 7, 2, 3, 'mica', 'pera', 2)
INSERT INTO [dbo].[Package] ([type], [weight], [price], [createTime], [acceptTime], [idAddressFrom], [idAddressTo], [status], [senderUserName], [courierUserName], [idAddress]) VALUES (1, 7.4, 1346.123, '20200605 10:34:09 AM', '20200606 7:21:53 PM', 2, 7, 2, 'rada', 'pera', 2)
INSERT INTO [dbo].[Package] ([type], [weight], [price], [createTime], [acceptTime], [idAddressFrom], [idAddressTo], [status], [senderUserName], [courierUserName], [idAddress]) VALUES (3, 5.9, 678413.123, '20200605 10:34:09 AM', '20200606 7:21:53 PM', 2, 7, 1, 'rada', NULL, 2)
INSERT INTO [dbo].[Package] ([type], [weight], [price], [createTime], [acceptTime], [idAddressFrom], [idAddressTo], [status], [senderUserName], [courierUserName], [idAddress]) VALUES (2, 89.5, 953413.123, '20200605 10:34:09 AM', '20200606 7:21:53 PM', 2, 7, 4, 'zika', NULL, 2)

--insert IsPickingUp
INSERT INTO [dbo].[IsPickingUp] ([idPackage], [userName], [stopNumber]) VALUES (4, 'pera', 1)
INSERT INTO [dbo].[IsPickingUp] ([idPackage], [userName], [stopNumber]) VALUES (5, 'pera', 2)

--insert Stockroom
INSERT INTO [dbo].[Stockroom] ([idAddress]) VALUES (1)
INSERT INTO [dbo].[Stockroom] ([idAddress]) VALUES (5)
INSERT INTO [dbo].[Stockroom] ([idAddress]) VALUES (8)

--insert Vehicle
INSERT INTO [dbo].[Vehicle] ([licensePlateNumber], [capacity], [fuelConsumption], [fuelType], [idStockroom]) VALUES ('BG8797AH', 1023.123, 5.7, 1, 1)
INSERT INTO [dbo].[Vehicle] ([licensePlateNumber], [capacity], [fuelConsumption], [fuelType], [idStockroom]) VALUES ('BG1892BB', 7895.568, 11.2, 2, 2)
INSERT INTO [dbo].[Vehicle] ([licensePlateNumber], [capacity], [fuelConsumption], [fuelType], [idStockroom]) VALUES ('VA2891JK', 868.892, 4.5, 0, 3)

--insert IsDriving
INSERT INTO [dbo].[IsDriving] ([userName], [licensePlateNumber], [distanceTraveled], [currentStopNumber], [remainingCapacity]) VALUES ('pera', 'BG1892BB', 159.592, 3, 6820.214)

--insert IsDelivering
INSERT INTO [dbo].[IsDelivering] ([idPackage], [userName], [stopNumber]) VALUES (4, 'pera', 1)
INSERT INTO [dbo].[IsDelivering] ([idPackage], [userName], [stopNumber]) VALUES (5, 'pera', 2)

--insert Drove
INSERT INTO [dbo].[Drove] ([userName], [licensePlateNumber]) VALUES ('pera', 'BG8797AH')
INSERT INTO [dbo].[Drove] ([userName], [licensePlateNumber]) VALUES ('pera', 'BG1892BB')
INSERT INTO [dbo].[Drove] ([userName], [licensePlateNumber]) VALUES ('mica', 'VA2891JK')
