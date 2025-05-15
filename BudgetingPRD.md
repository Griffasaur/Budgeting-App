Budgeting Android App

Overview:

This app is designed to be a basic, user-friendly budgeting tracker that is easy to use for those that may be less financially literate or are overwhelmed with the options presented in large-scale budgeting/banking apps. The goal of the app is to have users input their income and expenses, and then create "plans" which track how the user is doing based on the options from the plan.

Goals/Objectives:

- Provide a clean and not cluttered UI for inputting income, expenses and more.

- Calculate budget on a daily, weekly, bi-weekly, monthly, quarterly or annual basis.

- Update budget automatically based on what the user desires.

- Provide information to help users have a better understanding of their financial standing.

Target Audience:

The general consumer who may not be savvy on tracking their budget. While it could work for higher-earning individuals with more complex finances, the target user is a low/middle class user trying to save money.

Use Cases:

- As a user, I want to input my expenses, income and (later in development) debts.

- As a user, I want to set a plan for a specific time-frame to see my current budget.

- As a user, I want to see a clean UI displaying the positives and negatives of my current budget.

Features & Requirements:

- Adding transactions (income/expense) to view them in one place.

- Transactions will consist of: 
	- ID
	- Name
	- Type (income/expense)
	- Frequency
	- Description
	- Amount
	- Start Date
	- Next Date

- Creating a Plan which the user can select from different options to customize from

- Plans will consist of:
	- ID
	- Name
	- Type
	- Start Date
	- End Date
	- Frequency
	Depending on the type of plan, it may also include:
		- List of Income
		- List of Expenses
		- Remainder of total (Income - Expenses)
		- Previous term remainder
		- Goal Amounts
		- List of Debts

Technical Requirements:

Platform: Java
Minimum SDK: Android 8.0
Backend: Local storage initially, then cloud
Libraries/Tools: None specified or restricted for now

Design & UX:

I will upload the following wireframes separately, but for now I have:

	- Home
	- Income
	- Expenses
	- Plans
		- Plan View
		- Create Plan
		- Edit Plan
	- Settings

