CS201 Team 16 SimCity Project
======

### Important Project Links
  + [General Team Information](https://github.com/usc-csci201-fall2013/team16/wiki)
  + [Design Document List](https://github.com/usc-csci201-fall2013/team16/wiki/Design-Documents)

### Team Members & Primary Assignments
  + Justine Cocchi, Team Leader
  	+ Houses and homeowner role.
  	+ Apartments and landlord role.
  	+ restaurant4 design and integration.
  + Grant Derderian, Git/GitHub Expert
  	+ Market and associated roles (MarketWorker, MarketCustomer, MarketManager)
  	+ Global city timekeeping and base scheduling classes (PersonSchedule and PersonTask).
  	+ Program documentation design and Git upkeeping
  	+ restaurant3 design and integration.
  + Teryun (Tom) Lee
  	+ Master Bank class design
  	+ Individual bank branch design and associated roles (BankTeller, BankCustomer, BankManager)
  	+ restaurant5 design and integration.
  + Holly Mitchell
  	+ PersonAgent: main class design and data as well as main functions and schedule execution.
  	+ PersonAgent integration into roles and buildings.
  	+ Activity logging integration.
  	+ restaurant2 design and integration.
  + Trevor Reed, A* Expert
  	+ Global Transportation: buses, cars, market truck.
  	+ City GUI A*/Path Finding.
  	+ Main cityGui animation and overall graphic design.
  	+ restaurant1 design and integration.

#### v1 Team Assignment Notes

The initial design of the new SimCity components (markets, banks, GUI and layout, transporation, housing, etc.) was conceived through the collective work of all team members through various meetings we held several times per week for several hours. From there, specific assignments were distributed to each team member as noted above. Although one person took ownership of each major component, all team members contributed to each other's initial designs and final code.

Each team member contributed to the various GUI files within our program in order to ensure that each building was properly integrated into the main city layout. Additionally, we worked together in order to handle the overall successful integration of our own restaurant projects and individual buildings we were each responsible for.

Testing was conducted on a per-building or per-role basis within the testing folders named either per-person (i.e. "granttesting") or per single building unit (i.e. "BankTest").

#### v2 Team Assignment Notes

Much of the above stayed true into v2: each team member in our group continued to work on their previous assignment from v1 (as noted above) to ensure that their piece of the program worked well in collaboration with other parts of the program. After completing the majority of our indiviaul pieces of v1 and continuing to integrate improved versions of them in v2, we branched out into larger assignments that we all collaborated on, including:

  + Scheduling and job assignments based on time (and the continued collaborative development of PersonTask and PersonSchedule classes)
  + Truck deliveries from the market to each restaurant (and integrating the new market into each restaurant's different messages)
  + Autonomous transporation where people can intelligently determine what method of transporation they need to get anywhere in the city
  + Larger and more complex full-city scenarios that involve several interactions between different agents in roles
  
Much of our v2 development also focused on removing hacks and creating a truly autonomous city where people react to their own needs, tasks, and instincts (gotHungry, depositMoney, go home at the end of shift) as opposed to hacks that previously forced people to go these things.

### Compile Instructions (v1)
To compile and run our SimCity, clone this GitHub repo into a new directory on your local computer. Then, follow these instructios:
+ Open the Eclipse IDE on your computer.
+ Choose File -> New -> Other. Select the Java Project from Exisitng Ant Buildfile in the list of options
+ Click on Browse, navigate to the directory of the repo you cloned from out assignment on GitHub, and then select the build.xml file.
+ Click the Link to the buildfile in the file system checkbox and then click on finish.
+ Copy the `images` folder from the directory you cloned to your computer onto the top-most level folder of our project you just imported into Eclipse. When prompted, select the `Copy Files and Folders` option.

__To run the program as a whole__, navigate to the `CityGui.java` file located within `src/city.gui`. Then, click the small arrow next to the green play button in the Eclipse toolbar and select `CityGui` from the dropdown list. The program will now run. To close it, click the red stop sign in Eclipse.

__To test different buildings within the program__, navigate to any of the various test files located within the named project testing folders, such as `src/BankTest` or `src/city.transporation.test`. If you receive a message about JUnit not being in your build path, accept Eclipse's warning/suggestion to add it. If you don't see this, go to Project -> Properties in Eclipse's menu. Click on Java Build Path in the left menu, then on Libraries in the upper menu, and then on Add Library. Click JUnit, then select JUnit 3, and then click Finish. Click OK to exit the Project Settings dialog. With your selected test file still open, click the small arrow next to the green play button in the Eclipse toolbar and select the name of the test you would like to run from the dropdown list. That test will now run, showing you the results on the left.

### Running Scenarios
The scenarios listed in the World tab show the different scenarios that we have successfully implemented. Only one scenario can be run at a time, then the program must be restarted.

In the control panel, a specific scenario can be selected by the user and when "Start Scenario" is clicked, it will create people, send them to specific jobs, and perform the following actions:

+ **Full Scenario** - Our most robust execution of the program, this scenario tests grading rubric items A, B, and E. It adds people throughout the city, assigns them to each workplace (restaurants, markets, and banks) and also makes people use different transporation methods to get to each location. It additionally also triggers trucks to fulfill orders from certain restaurants through markets (and bills them afterward) and additionally causes certain appliances to break, causing the landlord to fix them (and also bill them for rent).
+ **The Weekender** - Forces the day to Saturday, closes Restaurants 2 and 4 as well as the bank, and creates people to try to access closed locations and go somewhere else when they can't go there.
+ **Trader Joe's** - Tests restaurant delivery from market as well as in person orders form hungry people throughout the city.
+ **Restaurant 1-5** - Tests customer, waiter, and host normative interaction (as well as market truck delivery) at a specific integrated restaurant in the city.
+ **Close Restaurants Test** - Demonstrates avoidance of people to closed restaurants. Restaurants can be manually closed, and then people will go to the next open one and see of they can eat there.
+ **Bank Test** - Creates exisiting accounts and tests several interactions with the bank, including making deposits, withdrawing various amounts of money, and taking out loans with various interest rates.
+ **Car Test** - Done automatically in most scenarios already (people are assigned cars which they then use to go around and do tasks they need to complete, such as eat or visit the market).
+ **Landlord Test** - Forces an appliance in an apartment to break, causing a person to message the landlord and have that appliance fixed.
+ **Market Truck Test** - Done automatically in most scenarios already (trucks are dispatched out to take and order and then deliver it to a market).
+ **Car Crash Test** - Can be triggered through button in the main world tab in the control panel.

Buttons to trigger car crashes, hit-and-runs, and the addition of specific people are available in the main World tab of the program's control panel.

### Overall State of Program

##### Transportation
+ Market Delivery Trucks are tested, but they have not been fully integrated yet. They show up in some scenarios to simply drive around to show the A* animation capabilities. They don't communicate with markets yet, except within tests.
+ Buses are fully tested and implemented, but we haven't yet updated the person to choose when to take the bus. We have a scenario that shows one person taking a bus, but we still need to implement this in a more general sense.
+ Cars are fully tested and implemented, but they haven't been integrated into the city yet. We just need to finish updating the Person Agent to include this form of transportation.

##### Animation
+ A* animation has been successfully implemented on a city-wide level to handle sidewalks, streets, and crosswalks. It hasn't been added inside of buildings yet. It sometimes lags when many guis are added to the panel, but they should eventually find their way to their destinations.

##### Person Agent
+ The PersonAgent needs to be redone so that there is an Event class which has an intent and a location. This is so that the person's actions and messages will be timed correctly when they take the bus or car. Right now, the Person's actions are based on the assumption that they can essentially be completed inside one method - there are semaphores within the DoGoTo method that take care of timing, but these get messed up when the person goes to the bus or takes the car. Instead, we now have a specific bus test which shows that the bus system is functional.

##### Housing
+ All houses and apartments are able to be clicked on and there is minimal gui functionality. The best scenario involving house gui components is the Average Joe scenario. There is no landlord gui capabilities at this time, however the landlord role is fully unit tested (rent collection and fixing appliances) as is the house (putting away groceries and cooking food).

#####Restaurants
+ All restaurants are integrated, both with their guis as well as their interaction with new elements in the city, including the market.
