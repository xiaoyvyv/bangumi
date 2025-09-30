---
is-library: true
title: Library of snippets
---

Any topic file marked with `is-library: true` is considered a library topic.
If you want an XML library, create an XML topic file with `is-library="true"` in the root `<topic>` element.

Library topics should not be used in instances,
which is why they do not require a title.
Writerside will not build library topics
because they can contain markup that is not valid
(as long as it is valid where it is included).

Although you can include any element with an ID from any topic file,
it is recommended to wrap reusable elements with `<snippet>`
and keep them in these dedicated library files.
You can have one library for the whole project
or several libraries for different groups of snippets.

Do not add library topics as `<toc-element>` in tree files,
except the dedicated tree file for the **Libraries** view.

<snippet id="first-snippet">

This is a snippet with a paragraph and a code block.
Add it ike this:

```xml
<include from="lib.md" element-id="first-snippet"/>
```

</snippet>

<snippet id="second-snippet">

You can add other snippets with reusable content.
Separate Markdown from the opening and closing `<snippet>` tags with a blank line.

</snippet>

<snippet id="sample">
    <note>Example note snippet</note>
</snippet>