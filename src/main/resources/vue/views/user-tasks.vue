<template id="user-tasks">
    <app-frame>
        <div class="row d-flex justify-content-center container">
            <div class="col-md-8">
                <div class="card-hover-shadow-2x mb-3 card border-info">
                    <div class="card-header-tab card-header text-center">
                        <div class="card-header-title font-size-lg text-capitalize font-weight-normal"><i class="fa fa-tasks"></i><h5 class="display-5"> Your Tasks, {{username}}!</h5></div>
                    </div>
                    <div class="scroll-area-sm">
                        <perfect-scrollbar class="ps-show-limits">
                            <div style="position: static;" class="ps ps--active-y">
                                <div class="ps-content">
                                    <ul class=" list-group list-group-flush">
                                        <li v-for="task in tasks" class="list-group-item">
                                            <div class="todo-indicator bg-warning"></div>
                                            <div class="widget-content p-0">
                                                <div class="widget-content-wrapper">
                                                    <div class="widget-content-left">
                                                        <div class="widget-heading"><span style="font-size: larger">{{task.title}} </span><div class="badge badge-danger ml-2">{{task.priority}}</div> <input v-bind:id="task.id" type="checkbox" v-on:change="markAsDone(task.id)" :checked="task.done === true">
                                                        </div>
                                                        <div class="widget-subheading" style="font-size: smaller">Category: {{task.category.name}} <br>Deadline: {{task.planed}}</div>
                                                    </div>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </perfect-scrollbar>
                    </div>
                    <div class="d-block text-center card-footer"><a href="login" class="mr-2 btn btn-link btn-sm">Logout</a><a href="new-category" class="btn btn-secondary">Create Category</a> <a href="new-task" class="btn btn-info">Add Task</a></div>
                </div>
            </div>
        </div>


    </app-frame>
</template>
<script>
    Vue.component("user-tasks", {
        template: "#user-tasks",
        data: () => ({
            username: localStorage.getItem("username"),
            tasks: [],
        }),
        created() {
            const token = localStorage.getItem("token");
            fetch("/api/me/tasks?token=" + token, {
                method: "GET",
            })
                .then( async res => {
                    if(res.status === 200) {
                        let resdata = await res.json();
                        console.log(resdata);
                        this.tasks = resdata;
                        console.log(this.tasks)
                    }
                })
                .catch(() => alert("Error while fetching tasks"));
        },
        methods: {
            markAsDone(id) {
                console.log(id)
                const token = localStorage.getItem("token");
                fetch("/api/me/tasks/" + id + "?token=" + token, {
                    method: "PUT"
                })
                    .then(async res => { if(res.status === 200) {
                        const data = await res.json();
                        location.pathname = "/tasks"
                    }})
                    .catch(() => alert("Error while doing"));
            }
        },
    });
</script>
