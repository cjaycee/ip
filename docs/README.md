# Usagi User Guide

Usagi is a simple task management chatbot that helps you keep track of your todos, deadlines, and events. Interact with Usagi through text commands to manage your task list efficiently.

## Quick Start

Launch Usagi and start managing your tasks with simple text commands. All your tasks are automatically saved to a file for persistence across sessions.

## Adding Todos

Add simple tasks without any time constraints.

**Usage:** `todo <description>`

**Example:** `todo buy groceries`

```
Got it. I've added this task:
  [T][ ] buy groceries
Now you have 1 task in the list.
```

## Adding Deadlines

Add tasks with specific due dates. Use the format: `yyyy-MM-dd`

**Usage:** `deadline <description> /by <yyyy-MM-dd>`

**Example:** `deadline eat cake /by 2025-09-09`

```
Got it. I've added this task:
[D][ ] eat cake (by: Sep 9 2025)
Now you have 2 tasks in the list.
```

## Adding Events

Add events with start and end times. Use the format: `yyyy-MM-ddTHH:mm`

**Usage:** `event <description> /from <yyyy-MM-ddTHH:mm> /to <yyyy-MM-ddTHH:mm>`

**Example:** `event conference /from 2025-09-09T12:00 /to 2025-09-09T14:00`

```
Got it. I've added this task:
[E][ ] conference (from: Sep 9 2025 12:00 to: Sep 9 2025 14:00)
Now you have 3 tasks in the list.
```

## Viewing Tasks

Display all tasks in your list with their current status.

**Usage:** `list`

**Example:** `list`

```
Here are the tasks in your list:
1. [T][ ] buy groceries
2. [D][ ] submit assignment (by: Sunday 11:59pm)
3. [E][ ] project meeting (from: Mon 2pm to: Mon 4pm)
```

## Marking Tasks

Mark tasks as completed or incomplete.

**Mark as done:** `mark <task number>`
**Mark as not done:** `unmark <task number>`

**Example:** `mark 1`

```
Nice! I've marked this task as done:
  [T][X] buy groceries
```

## Deleting Tasks

Remove tasks from your list permanently.

**Usage:** `delete <task number>`

**Example:** `delete 2`

```
Noted. I've removed this task:
  [D][ ] submit assignment (by: Sunday 11:59pm)
Now you have 2 tasks in the list.
```

## Finding Tasks

Search for tasks containing specific keywords.

**Usage:** `find <keyword>`

**Example:** `find meeting`

```
Here are the matching tasks in your list:
1. [E][ ] project meeting (from: Mon 2pm to: Mon 4pm)
```

## Duplicate Prevention

Usagi prevents you from adding duplicate tasks. If you try to add a task that already exists, you'll get a warning message instead.

**Example:** Trying to add `todo buy groceries` when it already exists:

```
Duplicate task: You already have this task in your task list!
```

## Greeting and Goodbye

**Start conversation:** `hi`
**End conversation:** `bye`

The `bye` command will close the application and save all your tasks automatically.

## Task Symbols

- `[T]` = Todo task
- `[D]` = Deadline task
- `[E]` = Event task
- `[X]` = Completed task
- `[ ]` = Incomplete task

## Error Handling

Usagi provides helpful error messages for common mistakes:

- **Empty descriptions:** "Empty description error: [command] description cannot be empty"
- **Invalid format:** "Format error: Use correct format for the command"
- **Invalid task numbers:** "Invalid task number: Please use a valid task number"
- **Invalid commands:** "Invalid command: I don't understand that command"