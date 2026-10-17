// A user may read their own documents.
permit (
  principal is User,
  action == Action::"read",
  resource is Document
)
when { resource.owner == principal };

// Locked documents cannot be read, even by the owner.
forbid (
  principal,
  action == Action::"read",
  resource is Document
)
when { resource.locked };
